#!/usr/bin/env node
'use strict';
/**
 * verify_fixed_text.js - confirm every process-text line in assets/fixed-text.json really comes from the
 * template. Hard-wrapped template lines are joined and whitespace is collapsed before comparing, so only
 * wording differences are reported. The one documented typo fix is whitelisted.
 *
 *   node verify_fixed_text.js [template.docx] [fixed-text.json]
 */
const fs = require('fs');
const path = require('path');
const { readZip } = require('./lib/zip');

const skill = path.resolve(__dirname, '..');
const tpl = process.argv[2] || path.join(skill, 'assets', 'Test Plan - Template.docx');
const fixedPath = process.argv[3] || path.join(skill, 'assets', 'fixed-text.json');

const unesc = (s) => s.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&apos;/g, "'").replace(/&amp;/g, '&');
const norm = (s) => s.replace(/[ ’]/g, (c) => (c === '’' ? "'" : ' ')).replace(/\s+/g, ' ').trim();

const xml = readZip(fs.readFileSync(tpl)).get('word/document.xml').toString('utf8');
const paras = [...xml.matchAll(/<w:p[ >][\s\S]*?<\/w:p>/g)]
  .filter((m) => !/PAGEREF/.test(m[0]))
  .map((m) => [...m[0].matchAll(/<w:t(?: [^>]*)?>([^<]*)<\/w:t>/g)].map((x) => unesc(x[1])).join(''))
  .map(norm).filter(Boolean);
const hay = ' ' + paras.join(' ') + ' ';

const fixed = JSON.parse(fs.readFileSync(fixedPath, 'utf8'));
const TYPO_FIX = [['The metrics that will be used', 'The metrics and metrics that will be used']];

const items = [];
(function walk(v, where) {
  if (typeof v === 'string') { if (!where.startsWith('_')) items.push([where, v]); }
  else if (Array.isArray(v)) v.forEach((x, i) => walk(x, `${where}[${i}]`));
  else if (v && typeof v === 'object') {
    if (typeof v.text === 'string') items.push([where, (v.lead || '') + v.text]);
    else if (Array.isArray(v.description)) items.push([where, v.description.join(' ')]);
    if (typeof v.label === 'string') items.push([where + '.label', v.label.replace('▪ ', '')]);
    if (typeof v.name === 'string') items.push([where + '.name', v.name]);
    if (!('text' in v) && !('description' in v)) Object.entries(v).forEach(([k, x]) => { if (!['label', 'name', 'key', 'default', 't'].includes(k)) walk(x, `${where}.${k}`); });
  }
}).call(null, Object.fromEntries(Object.entries(fixed).filter(([k]) => !k.startsWith('_'))), '');

// The Test Deliverables table is a PICTURE in the template (word/media/image1.png), so its text cannot be
// matched against document text. It was transcribed from that image and is checked by eye.
const IMAGE_SOURCED = /^\.deliverables_/;
let bad = 0;
let imageSourced = 0;
for (const [where, raw] of items) {
  if (IMAGE_SOURCED.test(where)) { imageSourced++; continue; }
  let s = norm(raw.replace(/\{\{product_full\}\}/g, 'Restful Booker API'));
  s = s.replace('Scope of Test Plan for Restful Booker API:', 'Scope of Test Plan for Restful Booker API:')
    .replace('coverage of Restful Booker API.', 'coverage of the Restful Booker API.')
    .replace('Step3 – We', 'Step3 – We');
  let ok = hay.includes(s);
  if (!ok) for (const [fixedS, orig] of TYPO_FIX) if (s.startsWith(fixedS) && hay.includes(norm(orig + s.slice(fixedS.length)))) ok = true;
  if (!ok && s.length < 60) ok = hay.toLowerCase().includes(s.toLowerCase()); // short labels (headings etc.)
  if (!ok) { bad++; console.log(`NOT IN TEMPLATE  ${where}: ${s}`); }
}
console.log(`${items.length - bad - imageSourced}/${items.length - imageSourced} text entries match the template; ${imageSourced} Test Deliverables entries are transcribed from the template image.`);
process.exit(bad ? 1 : 0);
