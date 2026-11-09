#!/usr/bin/env node
'use strict';
/**
 * build_test_plan.js - turn a Test Plan spec (JSON) into a .docx that follows the
 * "Test Plan - Template.docx" format: same section order, heading levels, tables,
 * header/watermark, page setup and process text. No npm dependencies (Node >= 18).
 *
 *   node build_test_plan.js --spec spec.json --out "Test Plan - X (KEY-1).docx"
 *        [--template <template.docx>] [--fixed <fixed-text.json>] [--no-update-fields]
 *
 * Anything the spec leaves empty is written as "Insufficient information to determine."
 * so the document never contains blank cells or invented values. The list of those
 * open items is printed at the end.
 */
const fs = require('fs');
const path = require('path');
const { readZip, writeZip } = require('./lib/zip');

const INSUFFICIENT = 'Insufficient information to determine.';
const SKILL_DIR = path.resolve(__dirname, '..');

// ------------------------------------------------------------------ CLI
function parseArgs(argv) {
  const a = { updateFields: true };
  for (let i = 0; i < argv.length; i++) {
    const k = argv[i];
    if (k === '--spec') a.spec = argv[++i];
    else if (k === '--out') a.out = argv[++i];
    else if (k === '--template') a.template = argv[++i];
    else if (k === '--fixed') a.fixed = argv[++i];
    else if (k === '--no-update-fields') a.updateFields = false;
    else if (k === '-h' || k === '--help') a.help = true;
    else throw new Error(`Unknown argument: ${k}`);
  }
  return a;
}

// ------------------------------------------------------------------ XML helpers
const clean = (s) => String(s).replace(/[\u0000-\u0008\u000B\u000C\u000E-\u001F]/g, '');
const esc = (s) => clean(s).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
const isBlank = (v) => v === undefined || v === null || (typeof v === 'string' && v.trim() === '');

const GREY = '666666';
const TABLE_BORDER = (sz = 8, color = '000000') =>
  ['top', 'left', 'bottom', 'right', 'insideH', 'insideV']
    .map((s) => `<w:${s} w:val="single" w:sz="${sz}" w:space="0" w:color="${color}"/>`).join('');

class Builder {
  constructor(fixed, spec) {
    this.fixed = fixed;
    this.spec = spec;
    this.body = [];
    this.headings = [];
    this.links = []; // {id, url}
    this.open = []; // places where INSUFFICIENT was written
    this.bm = 0;
  }

  fill(s) {
    return s.replace(/\{\{product_full\}\}/g, this.spec.product_full);
  }

  /** Value or the standard "unknown" sentence; records where it happened. */
  val(v, where) {
    if (isBlank(v)) {
      this.open.push(where);
      return INSUFFICIENT;
    }
    return String(v).trim();
  }

  // ---- runs
  run(text, rPr = '') {
    const parts = clean(text).split('\t');
    const inner = parts
      .map((t, i) => (i ? '<w:tab/>' : '') + (t === '' ? '' : `<w:t xml:space="preserve">${esc(t)}</w:t>`))
      .join('');
    return `<w:r>${rPr ? `<w:rPr>${rPr}</w:rPr>` : ''}${inner}</w:r>`;
  }

  /** Text with URLs turned into hyperlinks (same blue + underline as the template). */
  inline(text, rPr = '') {
    const s = clean(text);
    const re = /https?:\/\/[^\s<>"']+/g;
    let out = '';
    let last = 0;
    let m;
    while ((m = re.exec(s))) {
      let url = m[0];
      const trail = url.match(/[.,;:)\]]+$/);
      if (trail) url = url.slice(0, -trail[0].length);
      out += this.run(s.slice(last, m.index), rPr);
      const id = `rIdL${this.links.length + 1}`;
      this.links.push({ id, url });
      out += `<w:hyperlink r:id="${id}" w:history="1">${this.run(url, `${rPr}<w:color w:val="1155cc"/><w:u w:val="single"/>`)}</w:hyperlink>`;
      last = m.index + url.length;
      re.lastIndex = last;
    }
    out += this.run(s.slice(last), rPr);
    return out;
  }

  // ---- paragraphs
  para(inner, pPr = '') {
    return `<w:p><w:pPr>${pPr}</w:pPr>${inner}</w:p>`;
  }
  push(x) { this.body.push(x); }

  p(text, o = {}) {
    const sp = `<w:spacing w:before="${o.before || 0}" w:after="${o.after ?? 120}" w:line="276" w:lineRule="auto"/>`;
    this.push(this.para(this.inline(text, o.bold ? '<w:b w:val="1"/>' : ''), (o.keepNext ? '<w:keepNext/>' : '') + sp));
  }
  bold(text, o = {}) { this.p(text, { ...o, bold: true }); }

  /** Real Word bullet (numId 1 from the template's numbering.xml), as used under Objective. */
  bullet(text) {
    this.push(this.para(this.inline(text),
      '<w:numPr><w:ilvl w:val="0"/><w:numId w:val="1"/></w:numPr><w:spacing w:after="60" w:line="276" w:lineRule="auto"/><w:ind w:left="720" w:hanging="360"/>'));
  }
  /** Literal "•" list line as the template writes for criteria / strategy. */
  li(text, lead) {
    const body = (lead ? this.run(lead, '<w:b w:val="1"/>') : '') + this.inline(text);
    this.push(this.para(this.run('•\t') + body,
      '<w:spacing w:after="60" w:line="276" w:lineRule="auto"/><w:ind w:left="360" w:hanging="360"/>'));
  }
  /** Template's "o" sub-list (test design techniques, types of testing). */
  sub(text) {
    this.push(this.para(
      this.run('o\t', '<w:rFonts w:ascii="Courier New" w:hAnsi="Courier New" w:cs="Courier New"/>') + this.inline(text),
      '<w:spacing w:after="40" w:line="276" w:lineRule="auto"/><w:ind w:left="1080" w:hanging="360"/>'));
  }
  /** "- text" detail line under a numbered Scope item. */
  dash(text) {
    this.push(this.para(this.run('-\t') + this.inline(text),
      '<w:spacing w:after="40" w:line="276" w:lineRule="auto"/><w:ind w:left="540" w:hanging="270"/>'));
  }

  heading(level, text, o = {}) {
    const id = ++this.bm;
    const name = `_heading=h.tp${id}`;
    this.headings.push({ level, text, name });
    this.push(`<w:p><w:pPr><w:pStyle w:val="Heading${level}"/>${o.pageBreakBefore ? '<w:pageBreakBefore/>' : ''}</w:pPr>` +
      `<w:bookmarkStart w:id="${id}" w:name="${name}"/><w:bookmarkEnd w:id="${id}"/>${this.run(text)}</w:p>`);
  }

  // ---- tables (same borders, 100-twip cell margins and fixed layout as the template)
  table(widths, rows, o = {}) {
    const total = widths.reduce((a, b) => a + b, 0);
    const mar = ['top', 'left', 'bottom', 'right'].map((s) => `<w:${s} w:w="100" w:type="dxa"/>`).join('');
    const tr = rows.map((cells, ri) => {
      const header = ri === 0;
      const tcs = cells.map((c, ci) => {
        const lines = Array.isArray(c) ? c : [c];
        const fill = header && o.headerFill ? o.headerFill : 'auto';
        let rPr = header ? '<w:b w:val="1"/>' : '';
        if (header && o.headerColor) rPr += `<w:color w:val="${o.headerColor}"/>`;
        if (!header && o.bodyColor) rPr += `<w:color w:val="${o.bodyColor}"/>`;
        const jc = o.centerHeader && header ? '<w:jc w:val="center"/>' : '';
        const ps = lines.map((t) => this.para(this.inline(t, rPr),
          `<w:spacing w:before="0" w:after="0" w:line="240" w:lineRule="auto"/>${jc}`)).join('');
        return `<w:tc><w:tcPr><w:tcW w:w="${widths[ci]}" w:type="dxa"/><w:shd w:val="clear" w:color="auto" w:fill="${fill}"/>` +
          `<w:tcMar>${mar}</w:tcMar><w:vAlign w:val="top"/></w:tcPr>${ps}</w:tc>`;
      }).join('');
      return `<w:tr><w:trPr><w:cantSplit/>${header ? '<w:tblHeader/>' : ''}</w:trPr>${tcs}</w:tr>`;
    }).join('');
    this.push(`<w:tbl><w:tblPr><w:tblW w:w="${total}" w:type="dxa"/><w:jc w:val="left"/>` +
      `<w:tblBorders>${TABLE_BORDER(o.borderSz || 8, o.borderColor || '000000')}</w:tblBorders>` +
      `<w:tblLayout w:type="fixed"/><w:tblLook w:val="0600"/></w:tblPr>` +
      `<w:tblGrid>${widths.map((w) => `<w:gridCol w:w="${w}"/>`).join('')}</w:tblGrid>${tr}</w:tbl>`);
    this.push(this.para('', '<w:spacing w:after="0"/>')); // keeps a gap after the table
  }

  // ---- TOC (a real TOC field with the template's switches, hyperlinked entries)
  tocXml() {
    const entries = this.headings.map((h, i) => {
      const ind = (h.level - 2) * 360;
      const first = i === 0
        ? '<w:r><w:fldChar w:fldCharType="begin"/><w:instrText xml:space="preserve"> TOC \\h \\u \\z \\t &quot;Heading 1,1,Heading 2,2,Heading 3,3,Heading 4,4,Heading 5,5,Heading 6,6,&quot;</w:instrText><w:fldChar w:fldCharType="separate"/></w:r>'
        : '';
      const rp = `<w:rPr><w:color w:val="${GREY}"/></w:rPr>`;
      return `<w:p><w:pPr><w:tabs><w:tab w:val="right" w:leader="none" w:pos="9025"/></w:tabs>` +
        `<w:spacing w:before="60" w:line="240" w:lineRule="auto"/><w:ind w:left="${Math.max(ind, 0)}" w:firstLine="0"/></w:pPr>${first}` +
        `<w:hyperlink w:anchor="${h.name}"><w:r>${rp}<w:t xml:space="preserve">${esc(h.text)}</w:t></w:r></w:hyperlink>` +
        `<w:r>${rp}<w:tab/></w:r>` +
        `<w:r><w:fldChar w:fldCharType="begin"/><w:instrText xml:space="preserve"> PAGEREF ${h.name} \\h </w:instrText><w:fldChar w:fldCharType="separate"/></w:r>` +
        `<w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>`;
    }).join('');
    const closer = '<w:p><w:r><w:fldChar w:fldCharType="end"/></w:r></w:p>';
    return `<w:sdt><w:sdtPr><w:docPartObj><w:docPartGallery w:val="Table of Contents"/><w:docPartUnique w:val="1"/></w:docPartObj></w:sdtPr>` +
      `<w:sdtContent>${entries}${closer}</w:sdtContent></w:sdt>`;
  }
}

const HR = '<w:p><w:pPr><w:spacing w:after="120"/></w:pPr><w:r><w:pict><v:rect style="width:0.0pt;height:1.5pt" o:hr="t" o:hrstd="t" o:hralign="center" fillcolor="#A0A0A0" stroked="f"/></w:pict></w:r></w:p>';

// ------------------------------------------------------------------ spec checks
function checkSpec(spec) {
  const errors = [];
  if (isBlank(spec.product_short)) errors.push('product_short is required (short product name for the page-1 title)');
  if (isBlank(spec.product_full)) errors.push('product_full is required (product name used in Scope text)');
  const known = ['jira_key', 'product_short', 'product_full', 'author', 'objective', 'scope', 'inclusions',
    'environments', 'defect', 'schedule', 'deliverables', 'tools', 'risks'];
  const warn = Object.keys(spec).filter((k) => !known.includes(k) && !k.startsWith('_')).map((k) => `unknown spec key ignored: ${k}`);
  const arr = (k) => (spec[k] === undefined || Array.isArray(spec[k]) ? null : errors.push(`${k} must be an array`));
  ['scope', 'inclusions', 'tools', 'risks'].forEach(arr);
  for (const k of ['scope', 'inclusions']) {
    if (!Array.isArray(spec[k])) continue; // absent is fine; wrong type was reported above
    spec[k].forEach((g, i) => {
      if (!g || isBlank(g.title)) errors.push(`${k}[${i}].title is required`);
      if (g && g.points !== undefined && !Array.isArray(g.points)) errors.push(`${k}[${i}].points must be an array`);
    });
  }
  if (Array.isArray(spec.risks)) {
    spec.risks.forEach((r, i) => { if (!r || isBlank(r.risk)) errors.push(`risks[${i}].risk is required`); });
  }
  const objShape = [['objective', ['paragraphs', 'bullets']], ['environments', ['rows', 'platforms']], ['defect', ['rows']]];
  for (const [k, subs] of objShape) {
    if (spec[k] === undefined || spec[k] === null) continue;
    if (typeof spec[k] !== 'object' || Array.isArray(spec[k])) { errors.push(`${k} must be an object`); continue; }
    subs.forEach((s) => { if (spec[k][s] !== undefined && !Array.isArray(spec[k][s])) errors.push(`${k}.${s} must be an array`); });
  }
  return { errors, warn };
}

// ------------------------------------------------------------------ document assembly
function buildDocument(spec, fixed) {
  const b = new Builder(fixed, spec);
  const S = spec;
  const f = (s) => b.fill(s);

  // 1 Objective
  b.heading(2, 'Objective', { pageBreakBefore: true });
  const obj = S.objective || {};
  const paras = (obj.paragraphs || []).filter((x) => !isBlank(x));
  if (!paras.length) { b.p(b.val(null, 'Objective')); }
  paras.forEach((t) => b.p(t));
  (obj.bullets || []).filter((x) => !isBlank(x)).forEach((t) => b.bullet(t));

  // 2 Scope
  b.heading(2, 'Scope');
  b.p(f(fixed.scope_intro));
  if (!(S.scope || []).length) b.p(b.val(null, 'Scope'));
  (S.scope || []).forEach((g, i) => {
    const title = g.title.trim().replace(/:$/, '');
    b.push(b.para(b.run(`${i + 1}. `) + b.run(`${title}:`, '<w:b w:val="1"/>'),
      '<w:keepNext/><w:spacing w:before="120" w:after="40" w:line="276" w:lineRule="auto"/>'));
    const pts = (g.points || []).filter((x) => !isBlank(x));
    if (!pts.length) b.dash(b.val(null, `Scope > ${title}`));
    pts.forEach((t) => b.dash(t));
  });
  b.p(f(fixed.scope_closing), { before: 160 });

  // 3 Inclusions
  b.heading(3, 'Inclusions');
  if (!(S.inclusions || []).length) b.p(b.val(null, 'Inclusions'));
  (S.inclusions || []).forEach((g) => {
    const title = g.title.trim().replace(/:$/, '');
    b.bold(`${title}:`, { before: 120, after: 60, keepNext: true });
    const pts = (g.points || []).filter((x) => !isBlank(x));
    if (!pts.length) b.p(b.val(null, `Inclusions > ${title}`), { after: 60 });
    pts.forEach((t) => b.p(t, { after: 60 }));
  });

  // 4 Test Environments
  b.heading(3, 'Test Environments');
  fixed.environments_intro.forEach((t) => b.p(t, { after: 80 }));
  const env = S.environments || {};
  let envRows = (env.rows || []).filter((r) => r && (!isBlank(r.name) || !isBlank(r.url)));
  if (!envRows.length) envRows = [{}];
  b.p('', { after: 60 });
  b.table([4513, 4513], [fixed.environments_table_header,
    ...envRows.map((r, i) => [b.val(r.name, `Test Environments > row ${i + 1} Name`), b.val(r.url, `Test Environments > row ${i + 1} Env url`)])]);
  const plats = (env.platforms || []).filter((x) => !isBlank(x));
  if (!plats.length) b.p(b.val(null, 'Test Environments > platforms/browsers/devices'));
  plats.forEach((t) => b.li(t));

  // 5 Defect Reporting Procedure
  b.heading(3, 'Defect Reporting Procedure');
  fixed.defect_intro.forEach((t) => b.p(t, { after: 80 }));
  const def = S.defect || {};
  let defRows = (def.rows || []).filter((r) => r && (!isBlank(r.process) || !isBlank(r.poc)));
  if (!defRows.length) defRows = [{}];
  b.p('', { after: 60 });
  b.table([4513, 4513], [fixed.defect_table_header,
    ...defRows.map((r, i) => [b.val(r.process, `Defect Reporting > row ${i + 1} Defect Process`), b.val(r.poc, `Defect Reporting > row ${i + 1} POC`)])]);
  b.p(`Tools - ${b.val(def.tool, 'Defect Reporting > Tools')}`);

  // 6 Test Strategy (fixed)
  b.heading(3, 'Test Strategy');
  fixed.strategy.forEach((x) => {
    if (x.t === 'p') b.p(x.text, { after: 80 });
    else if (x.t === 'li') b.li(x.text, x.lead);
    else if (x.t === 'sub') b.sub(x.text);
  });

  // 7 Test Schedule
  b.heading(3, 'Test Schedule');
  fixed.schedule_intro.forEach((t, i) => b.p(t, { after: i === fixed.schedule_intro.length - 1 ? 120 : 0 }));
  const sd = (S.schedule && S.schedule.dates) || {};
  b.table([4513, 4513], [fixed.schedule_table_header,
    ...fixed.schedule_rows.map((r) => [r.label, b.val(sd[r.key], `Test Schedule > ${r.label.replace('▪ ', '')}`)])]);
  b.bold(isBlank(S.schedule && S.schedule.duration)
    ? `Sprints to Test the Application: ${b.val(null, 'Test Schedule > duration')}`
    : S.schedule.duration.trim());

  // 8 Test Deliverables
  b.heading(3, 'Test Deliverables.');
  b.p(fixed.deliverables_intro, { after: 80, keepNext: true });
  const dd = (S.deliverables && S.deliverables.dates) || {};
  b.table([1900, 4926, 2200], [fixed.deliverables_table_header,
    ...fixed.deliverables_rows.map((r) => [r.name, r.description,
      isBlank(dd[r.key]) && r.default ? r.default : b.val(dd[r.key], `Test Deliverables > ${r.name} date`)])],
  { headerFill: 'BF2020', headerColor: 'FFFFFF', bodyColor: '800000', centerHeader: true });

  // 9 Entry and Exit Criteria
  b.heading(3, 'Entry and Exit Criteria');
  b.p(fixed.criteria_intro, { after: 60 });
  fixed.criteria_blocks.forEach((x) => {
    if (x.t === 'p') b.p(x.text, { after: 60 });
    else if (x.t === 'h3') b.heading(3, x.text);
    else if (x.t === 'h4') b.heading(4, x.text);
    else if (x.t === 'li') b.li(x.text);
  });

  // Tools / Risks / Approvals (Heading 4 in the template)
  b.heading(4, 'Tools');
  b.p(fixed.tools_intro, { after: 60 });
  const tools = (S.tools || []).filter((x) => !isBlank(x));
  if (!tools.length) b.li(b.val(null, 'Tools'));
  tools.forEach((t) => b.li(t));

  b.heading(4, 'Risks and Mitigations');
  b.p(fixed.risks_intro, { after: 60 });
  const risks = (S.risks || []).filter((r) => r && !isBlank(r.risk));
  if (!risks.length) b.p(b.val(null, 'Risks and Mitigations'));
  risks.forEach((r, i) => {
    b.push(b.para(b.run('Risk: ', '<w:b w:val="1"/>') + b.inline(r.risk.trim()),
      '<w:keepNext/><w:spacing w:before="80" w:after="0" w:line="276" w:lineRule="auto"/>'));
    b.push(b.para(b.run('Mitigation: ', '<w:b w:val="1"/>') + b.inline(b.val(r.mitigation, `Risks > risk ${i + 1} mitigation`)),
      '<w:spacing w:before="0" w:after="60" w:line="276" w:lineRule="auto"/>'));
  });

  b.heading(4, 'Approvals');
  fixed.approvals.forEach((x) => {
    if (x.t === 'p') b.p(x.text, { after: 60 });
    else if (x.t === 'li') b.li(x.text);
  });

  return b;
}

// ------------------------------------------------------------------ package assembly
function runBounds(xml, text) {
  const i = xml.indexOf(`<w:t xml:space="preserve">${text}</w:t>`);
  if (i < 0) return null;
  const s = Math.max(xml.lastIndexOf('<w:r>', i), xml.lastIndexOf('<w:r ', i));
  const e = xml.indexOf('</w:r>', i) + 6;
  return { s, e };
}

function patchHeader(xml, spec) {
  // Page-1 header: "Created by <author> - Test Plan (<product>)" + TheTestingAcademy.com
  let r = runBounds(xml, 'Test Plan (Restful Booker)');
  if (!r) throw new Error('Template header: title run not found');
  xml = xml.slice(0, r.s) +
    xml.slice(r.s, r.e).replace('Test Plan (Restful Booker)', esc(`Test Plan (${spec.product_short.trim()})`)) + xml.slice(r.e);
  r = runBounds(xml, 'Created by Pramod  Dutta  - ');
  if (!r) throw new Error('Template header: "Created by" run not found');
  const author = isBlank(spec.author) ? null : spec.author.trim();
  const run = xml.slice(r.s, r.e);
  xml = xml.slice(0, r.s) + (author ? run.replace('Created by Pramod  Dutta  - ', esc(`Created by ${author} - `)) : '') + xml.slice(r.e);
  return xml;
}

function patchRels(xml, links) {
  const keep = [...xml.matchAll(/<Relationship [^>]*\/>/g)].map((m) => m[0])
    .filter((t) => !/relationships\/(image|hyperlink)"/.test(t));
  const add = links.map((l) => `<Relationship Id="${l.id}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink" Target="${esc(l.url)}" TargetMode="External"/>`);
  const head = xml.slice(0, xml.indexOf('<Relationship '));
  return `${head}${[...keep, ...add].join('')}</Relationships>`;
}

function main() {
  const args = parseArgs(process.argv.slice(2));
  if (args.help || !args.spec || !args.out) {
    console.log('Usage: node build_test_plan.js --spec spec.json --out out.docx [--template t.docx] [--fixed fixed-text.json] [--no-update-fields]');
    process.exit(args.help ? 0 : 2);
  }
  const spec = JSON.parse(fs.readFileSync(args.spec, 'utf8'));
  const fixed = JSON.parse(fs.readFileSync(args.fixed || path.join(SKILL_DIR, 'assets', 'fixed-text.json'), 'utf8'));
  const { errors, warn } = checkSpec(spec);
  if (errors.length) { console.error('Spec errors:\n - ' + errors.join('\n - ')); process.exit(1); }
  warn.forEach((w) => console.warn('warning: ' + w));

  const tplPath = args.template || path.join(SKILL_DIR, 'assets', 'Test Plan - Template.docx');
  const files = readZip(fs.readFileSync(tplPath));
  const get = (n) => { if (!files.has(n)) throw new Error(`Template is missing ${n}`); return files.get(n).toString('utf8'); };

  const b = buildDocument(spec, fixed);

  // document.xml: keep the template's root element (namespaces) and sectPr (page size, header/footer refs, title page)
  const tplDoc = get('word/document.xml');
  const root = tplDoc.match(/<w:document[^>]*>/)[0];
  const sect = tplDoc.slice(tplDoc.lastIndexOf('<w:sectPr'), tplDoc.lastIndexOf('</w:body>'));
  const doc = `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>${root}<w:body>${HR}${b.tocXml()}${HR}${b.body.join('')}${sect}</w:body></w:document>`;
  files.set('word/document.xml', doc);
  files.set('word/header1.xml', patchHeader(get('word/header1.xml'), spec));
  files.set('word/_rels/document.xml.rels', patchRels(get('word/_rels/document.xml.rels'), b.links));
  for (const n of [...files.keys()]) if (n.startsWith('word/media/')) files.delete(n); // template screenshots are project-specific
  if (args.updateFields) {
    // Word refreshes the TOC page numbers on open (it asks once). scripts/refresh_toc.ps1 can bake them in instead.
    files.set('word/settings.xml', get('word/settings.xml').replace('<w:compat>', '<w:updateFields w:val="true"/><w:compat>'));
  }

  fs.mkdirSync(path.dirname(path.resolve(args.out)), { recursive: true });
  fs.writeFileSync(args.out, writeZip(files));

  console.log(`Wrote ${args.out}`);
  console.log(`Sections: ${b.headings.length} headings | hyperlinks: ${b.links.length}`);
  console.log(`Open items ("${INSUFFICIENT}"): ${b.open.length}`);
  b.open.forEach((o) => console.log(`  - ${o}`));
}

main();
