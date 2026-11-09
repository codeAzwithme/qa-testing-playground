<#
.SYNOPSIS
  Optional (Windows + Microsoft Word only): bake the Table of Contents page numbers into a generated Test Plan.

.DESCRIPTION
  build_test_plan.js writes a real TOC field but cannot know page numbers without a layout engine, so it sets
  "update fields on open" and Word asks once when the file is opened. Run this script to have Word do that update
  now and save the file, so the TOC shows page numbers in any viewer.

  Uses its own Word instance and never touches other running Word windows or documents.

.EXAMPLE
  powershell -NoProfile -ExecutionPolicy Bypass -File refresh_toc.ps1 -Path "C:\plans\Test Plan - X (KEY-1).docx"
#>
param([Parameter(Mandatory = $true)][string]$Path)

$ErrorActionPreference = 'Stop'
$full = (Resolve-Path -LiteralPath $Path).Path
$word = $null
try {
  $word = New-Object -ComObject Word.Application
} catch {
  Write-Output 'Microsoft Word is not available; skipped. Word will offer to update fields when the file is opened (click Yes).'
  exit 0
}
try {
  $word.Visible = $false
  $word.DisplayAlerts = 0
  $doc = $word.Documents.Open($full, $false, $false, $false)
  $doc.Fields.Update() | Out-Null
  foreach ($toc in $doc.TablesOfContents) { $toc.Update() }
  $doc.Save()
  Write-Output ("TOC refreshed. Pages: " + $doc.ComputeStatistics(2))
  $doc.Close(0)
} finally {
  try { $word.Quit() } catch {}
}
