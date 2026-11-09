#!/usr/bin/env node
'use strict';
// Programmatic grader for the Test Plan skill evals. Writes grading.json (text/passed/evidence) next to each run.
//   node grade.js <iteration-dir>
const fs = require('fs');
const path = require('path');
const { execFileSync } = require('child_process');

const SKILL = path.resolve(__dirname, '..', '..', 'jira-test-plan-generator');
const iter = path.resolve(process.argv[2]);
const inspect = (docx, flag) => execFileSync('node', [path.join(SKILL, 'scripts', 'inspect_docx.js'), docx, flag], { encoding: 'utf8', stdio: ['ignore', 'pipe', 'pipe'] });

const HEADERS = [['Name', 'Env url'], ['Defect Process', 'POC'], ['Task', 'Dates'], ['Deliverables', 'Description', 'Target Completion Date']];
const RESIDUE = ['Restful Booker', 'restful-booker', 'herokuapp', 'Pramod', 'Sprint boot', 'Devesh', 'Sonal', 'Prajeeth', 'Pre Prod', 'flagger'];
const EXTRA_SECTIONS = ['Assumptions', 'Open Questions', 'Traceability', 'Gaps'];

function commonChecks(docx, text) {
  const r = [];
  let ok = true, ev = '';
  try { ev = inspect(docx, '--check').trim(); } catch (e) { ok = false; ev = String(e.stderr || e.message).slice(0, 300); }
  r.push({ text: 'Heading order and levels match the template (20 headings)', passed: ok, evidence: ev });

  const tables = text.split('\n').filter((l) => l === '[TABLE]').length;
  const lines = text.split('\n');
  const hdrOk = HEADERS.every((h) => lines.some((l) => l.trim() === '| ' + h.join(' | ')));
  r.push({ text: 'Four tables with the template column headers (Name/Env url, Defect Process/POC, Task/Dates, Deliverables/Description/Target Completion Date)', passed: tables === 4 && hdrOk, evidence: `${tables} tables; headers present: ${hdrOk}` });

  const found = RESIDUE.filter((w) => text.includes(w));
  r.push({ text: 'No leftover content from the template example project', passed: found.length === 0, evidence: found.length ? 'found: ' + found.join(', ') : 'none found' });

  const heads = lines.filter((l) => /^<Heading\d>/.test(l));
  const extra = heads.filter((h) => EXTRA_SECTIONS.some((s) => h.includes(s)));
  r.push({ text: 'No sections beyond the template (no Assumptions / Open Questions / Traceability)', passed: extra.length === 0, evidence: extra.length ? extra.join('; ') : `${heads.length} headings, none extra` });

  const fixed = ['Step 2: Our testing procedure when we receive a request for testing:', 'Testing will only continue to the next steps once these approvals are done', 'Doubts are cleared'];
  const miss = fixed.filter((f) => !text.includes(f));
  r.push({ text: 'Fixed template text present verbatim (strategy, entry/exit criteria, approvals)', passed: miss.length === 0, evidence: miss.length ? 'missing: ' + miss.join(' | ') : 'all present' });
  return r;
}

const SPECIFIC = {
  'vwo-prd-ticket': (text) => {
    const r = [];
    const ids = ['FR1', 'FR2', 'FR3', 'FR4', 'FR5', 'FR6', 'FR7', 'FR8', 'FR9'];
    const miss = ids.filter((i) => !new RegExp(`\\b${i}\\b`).test(text));
    r.push({ text: 'All nine requirement IDs FR1-FR9 appear', passed: !miss.length, evidence: miss.length ? 'missing ' + miss.join(',') : 'all present' });
    r.push({ text: 'PRD priorities are carried: Must (FR1, FR2, FR3, FR4, FR6), High (FR5, FR7, FR8), Medium (FR9)', passed: text.includes('Must (FR1, FR2, FR3, FR4, FR6)') && text.includes('High (FR5, FR7, FR8)') && text.includes('Medium (FR9)'), evidence: 'checked literal text' });
    const nfr = ['2 seconds', '2FA', 'GDPR', '99.9%', 'high visitor volumes'];
    const nm = nfr.filter((n) => !text.includes(n));
    r.push({ text: 'Five non-functional requirements are present (2 s response, 2FA/RBAC, GDPR/CCPA, 99.9% SLA, visitor volume)', passed: !nm.length, evidence: nm.length ? 'missing ' + nm.join(',') : 'all present' });
    const risks = ['Technical Complexity', 'Data Accuracy Challenges', 'User Adoption'];
    r.push({ text: 'Three PRD risks with their mitigations', passed: risks.every((x) => text.includes('Risk: ' + x)) && (text.match(/Mitigation: (?!Insufficient)/g) || []).length === 3, evidence: `mitigations with content: ${(text.match(/Mitigation: (?!Insufficient)/g) || []).length}` });
    r.push({ text: 'Product URL from the ticket is in the Test Environments table', passed: /\| https:\/\/app\.vwo\.com\/\s*$/m.test(text), evidence: 'table row checked' });
    r.push({ text: 'No invented dates (no ISO dates anywhere in the plan)', passed: !/\b20\d\d-\d\d-\d\d\b/.test(text), evidence: 'regex for yyyy-mm-dd found none' });
    r.push({ text: 'No invented platform line such as "Windows 10 - Chrome"', passed: !/Windows 10\s*[–-]\s*Chrome/.test(text), evidence: 'absent' });
    return r;
  },
  'thin-pasted-ticket': (text) => {
    const r = [];
    r.push({ text: 'Story content present: "Promo code" field, "Apply" button, valid and invalid code', passed: text.includes('"Promo code"') && text.includes('"Apply"') && /valid code/.test(text) && /invalid code/.test(text), evidence: 'literal checks' });
    r.push({ text: 'Priority High and key SHOP-12 are carried', passed: text.includes('SHOP-12') && text.includes('Priority: High'), evidence: 'literal checks' });
    r.push({ text: 'Due date 2026-12-15 appears once, in the duration line, not in a schedule row', passed: (text.match(/2026-12-15/g) || []).length === 1 && /^Jira due date for SHOP-12: 2026-12-15\./m.test(text), evidence: `${(text.match(/2026-12-15/g) || []).length} occurrence(s)` });
    r.push({ text: 'Error message text is not invented', passed: text.includes('Error message text: Insufficient information to determine.'), evidence: 'literal check' });
    const n = (text.match(/Insufficient information to determine\./g) || []).length;
    r.push({ text: 'At least 10 open items (environments, owners, tools, schedule, deliverable dates, risks)', passed: n >= 10, evidence: `${n} occurrences` });
    r.push({ text: 'Tools and Risks sections show the gap, not generic examples', passed: !/Backup Resource Planning|Mind map Tool|Non-Availability of a Resource/.test(text), evidence: 'template example risks/tools absent' });
    return r;
  },
};

let total = 0, pass = 0;
for (const evalDir of fs.readdirSync(iter).filter((d) => d.startsWith('eval-'))) {
  const name = evalDir.replace(/^eval-\d+-/, '');
  const outDir = path.join(iter, evalDir, 'with_skill', 'outputs');
  let expectations;
  const docx = fs.existsSync(outDir) ? fs.readdirSync(outDir).filter((f) => f.endsWith('.docx')) : [];
  if (name === 'missing-jira-key') {
    expectations = [{ text: 'No .docx is produced for a key that does not exist', passed: docx.length === 0, evidence: `${docx.length} docx file(s) in outputs` }];
    const note = path.join(outDir, 'response.md');
    if (fs.existsSync(note)) {
      const t = fs.readFileSync(note, 'utf8');
      expectations.push({ text: 'Response says TES-999 was not found and asks for the right key or access', passed: /not found/i.test(t) && /TES-999/.test(t) && /key|access/i.test(t), evidence: 'response.md checked' });
    }
  } else {
    if (docx.length !== 1) { expectations = [{ text: 'Exactly one .docx produced', passed: false, evidence: `${docx.length} found` }]; }
    else {
      const file = path.join(outDir, docx[0]);
      const text = inspect(file, '--text');
      expectations = [...commonChecks(file, text), ...(SPECIFIC[name] ? SPECIFIC[name](text) : [])];
    }
  }
  const p = expectations.filter((e) => e.passed).length;
  total += expectations.length; pass += p;
  fs.writeFileSync(path.join(iter, evalDir, 'with_skill', 'grading.json'), JSON.stringify({ expectations, summary: { passed: p, failed: expectations.length - p, total: expectations.length, pass_rate: +(p / expectations.length).toFixed(3) } }, null, 2));
  console.log(`${evalDir}: ${p}/${expectations.length}`);
  expectations.filter((e) => !e.passed).forEach((e) => console.log(`   FAIL: ${e.text} -> ${e.evidence}`));
}
console.log(`TOTAL ${pass}/${total}`);
