# Reading the requirement from Jira

Goal: collect every fact the ticket holds, and nothing else, before writing the spec.

## Contents
1. Tools
2. Steps
3. What counts as a source
4. When something goes wrong
5. No Jira tool available

## 1. Tools

This skill uses an Atlassian (Jira) MCP server with read access. Tool names differ between clients. In Claude
the usual ones are `getAccessibleAtlassianResources`, `getJiraIssue`, `listJiraIssueComments` and
`searchJiraIssuesUsingJql`. Some clients load MCP tools on demand, so search for them by name first if they are
not already callable. Only read operations are needed. Do not create or edit issues.

## 2. Steps

1. **Resolve the site.** If the user gave a Jira URL, the host name is the site. Otherwise call
   `getAccessibleAtlassianResources` once and reuse the `cloudId`. If several sites come back and the key does not
   say which one, ask the user.
2. **Get the issue.** `getJiraIssue` with the key, in the most complete view the tool offers ("full" or
   "evidence"), so the description, fields and links come back. The description often holds the whole requirement,
   including tables flattened into lines.
3. **Get comments** with `listJiraIssueComments` when the issue reports any. Acceptance criteria and clarified
   scope are often there.
4. **Get children.** If the issue is an Epic, has sub-tasks, or the type suggests a parent of stories, run
   `searchJiraIssuesUsingJql` with `parent = KEY` (and `"Epic Link" = KEY` on older sites) and read each child's
   summary, description and status. Requirements are often spread across stories.
5. **Get linked issues.** For each entry in the issue links, read summary and description. Treat them as
   supporting material, and name them in your report.
6. **Note attachments.** You usually cannot read attachment contents through the tool. List their file names in
   your report as "not read" so the user knows a PRD file may hold more than the plan shows.
7. **Fields worth reading:** summary, issue type, status, priority, labels, components, fix versions, due date,
   sprint, assignee, reporter, and custom fields such as acceptance criteria when they appear.

## 3. What counts as a source

Allowed: the issue and the issues you fetched in steps 3 to 5, and anything the user pasted or said in this
conversation. Not allowed: knowledge of the product from memory, its public website, similar test plans, or what
is "typical" for this kind of system. If the ticket quotes a URL, you may cite the URL, you may not browse it for
more requirements unless the user asks.

Note the ticket's wording when it is tagged as uncertain ("to be confirmed", "assumed", "TBD"). Carry the
uncertainty into the plan line instead of stating it as settled.

## 4. When something goes wrong

- **Issue not found or no permission:** stop. Tell the user the key, the site you tried, and the error. Do not
  produce a plan. Ask for the correct key or for access.
- **Issue found but nearly empty** (no description, no children, no comments): say so, and ask whether to
  continue. If the user wants the file anyway, build it. Almost every project-specific cell will read
  `Insufficient information to determine.`, which is the correct output.
- **Tool not connected or login needed:** tell the user, and use section 5.

## 5. No Jira tool available

Ask the user to paste the issue text (summary, description, comments, key fields) or point to an exported
file. Work from that, and say in the report that the source was pasted text and not a live read of the key.
Do not ask for API tokens or passwords, and do not try to log in on the user's behalf.
