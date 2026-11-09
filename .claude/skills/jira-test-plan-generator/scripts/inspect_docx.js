#!/usr/bin/env node
'use strict';
/**
 * inspect_docx.js - print the structure of a .docx (headings, tables, text) so a generated
 * Test Plan can be checked against the template without opening Word.
 *
 *   node inspect_docx.js <file.docx> [--text]     outline only (default) or full text in order
 *   node inspect_docx.js <file.docx> --check      compare heading sequence with the template's, exit 1 on mismatch
 */
const fs = require('fs');
const { readZip } = require('./lib/zip');

const EXPECTED = [
  [2, 'Objective'], [2, 'Scope'], [3, 'Inclusions'], [3, 'Test Environments'], [3, 'Defect Reporting Procedure'],
  [3, 'Test Strategy'], [3, 'Test Schedule'], [3, 'Test Deliverables.'], [3, 'Entry and Exit Criteria'],
  [4, 'Entry Criteria:'], [4, 'Exit Criteria:'], [3, 'Test Execution'], [4, 'Entry Criteria:'], [4, 'Exit Criteria:'],
  [3, 'Test Closure'], [4, 'Entry Criteria:'], [4, 'Exit Criteria:'], [4, 'Tools'], [4, 'Risks and Mitigations'], [4, 'Approvals'],
];

const unesc = (s) => s.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&apos;/g, "'").replace(/&amp;/g, '&');
const text = (x) => unesc([...x.matchAll(/<w:t(?: [^>]*)?>([^<]*)<\/w:t>|<w:tab\/>/g)].map((m) => (m[1] !== undefined ? m[1] : '\t')).join(''));

/** Walk body in order -> [{kind:'p'|'table', style, text, rows}] (TOC paragraphs are marked toc:true). */
function blocks(xml) {
  const body = xml.slice(xml.indexOf('<w:body>') + 8, xml.lastIndexOf('</w:body>'));
  const out = [];
  let i = 0;
  const sdtRanges = [];
  for (const m of body.matchAll(/<w:sdt>[\s\S]*?<\/w:sdt>/g)) sdtRanges.push([m.index, m.index + m[0].length]);
  const inSdt = (n) => sdtRanges.some(([a, b]) => n >= a && n < b);
  const re = /<w:tbl>|<w:p[ >]/g;
  let m;
  while ((m = re.exec(body))) {
    if (m[0] === '<w:tbl>') {
      let depth = 0; const tre = /<w:tbl>|<\/w:tbl>/g; tre.lastIndex = m.index; let t; let end = m.index;
      while ((t = tre.exec(body))) { depth += t[0] === '<w:tbl>' ? 1 : -1; if (!depth) { end = tre.lastIndex; break; } }
      const tbl = body.slice(m.index, end);
      const rows = [...tbl.matchAll(/<w:tr[ >][\s\S]*?<\/w:tr>/g)].map((r) =>
        [...r[0].matchAll(/<w:tc>[\s\S]*?<\/w:tc>/g)].map((c) => [...c[0].matchAll(/<w:p[ >][\s\S]*?<\/w:p>/g)].map((p) => text(p[0])).filter(Boolean).join(' / ')));
      out.push({ kind: 'table', rows });
      re.lastIndex = end;
    } else {
      const end = body.indexOf('</w:p>', m.index) + 6;
      const p = body.slice(m.index, end);
      out.push({ kind: 'p', toc: inSdt(m.index), style: (p.match(/<w:pStyle w:val="([^"]+)"/) || [])[1] || '', text: text(p) });
      re.lastIndex = end;
    }
  }
  return out;
}

function main() {
  const [file, ...flags] = process.argv.slice(2);
  if (!file) { console.error('Usage: node inspect_docx.js <file.docx> [--text|--check]'); process.exit(2); }
  const files = readZip(fs.readFileSync(file));
  const bl = blocks(files.get('word/document.xml').toString('utf8'));
  const heads = bl.filter((b) => b.kind === 'p' && !b.toc && /^Heading\d$/.test(b.style) && b.text.trim())
    .map((b) => [+b.style.slice(7), b.text.trim()]);

  if (flags.includes('--check')) {
    const a = JSON.stringify(heads), e = JSON.stringify(EXPECTED);
    if (a === e) { console.log(`OK: ${heads.length} headings match the template order and levels.`); return; }
    console.error('MISMATCH in headings.\nexpected: ' + e + '\nactual:   ' + a);
    process.exit(1);
  }
  if (flags.includes('--text')) {
    for (const b of bl) {
      if (b.toc) continue;
      if (b.kind === 'table') { console.log('[TABLE]'); b.rows.forEach((r) => console.log('  | ' + r.join(' | '))); }
      else if (b.text.trim() || b.style) console.log((b.style ? `<${b.style}> ` : '') + b.text);
    }
    return;
  }
  heads.forEach(([l, t]) => console.log(`${'  '.repeat(l - 2)}H${l} ${t}`));
  const tables = bl.filter((b) => b.kind === 'table');
  console.log(`\ntables: ${tables.length}`);
  tables.forEach((t, i) => console.log(`  T${i + 1}: ${t.rows.length} rows x ${t.rows[0].length} cols | header: ${t.rows[0].join(' | ')}`));
  const placeholders = bl.reduce((n, b) => n + ((b.kind === 'p' ? b.text : b.rows.flat().join(' ')).match(/Insufficient information to determine\./g) || []).length, 0);
  console.log(`"Insufficient information to determine." occurrences: ${placeholders}`);
}

main();
