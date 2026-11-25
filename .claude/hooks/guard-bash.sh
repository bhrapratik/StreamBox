#!/bin/bash
# PreToolUse hook (Bash): blocks dangerous shell commands before they run.
# Exit 2 = block the call and show stderr to Claude. Exit 0 = allow.
cmd=$(python3 -c 'import json,sys; print(json.load(sys.stdin).get("tool_input",{}).get("command",""))')

block() {
  echo "BLOCKED by guard-bash: $1. Command: '$cmd'. Stop and ask the user." >&2
  exit 2
}

# Force push (any form)
echo "$cmd" | grep -qE 'git[[:space:]]+push.*(--force|--force-with-lease|[[:space:]]-f([[:space:]]|$))' \
  && block "force-push is not allowed"
# Push to main (git push origin main, HEAD:main, ...)
echo "$cmd" | grep -qE 'git[[:space:]]+push.*([[:space:]]|:)main([[:space:]]|$)' \
  && block "pushing to main is not allowed; push a branch and open a PR"
# Recursive force delete
echo "$cmd" | grep -qE 'rm[[:space:]]+-[a-zA-Z]*r[a-zA-Z]*f|rm[[:space:]]+-[a-zA-Z]*f[a-zA-Z]*r' \
  && block "rm -rf is not allowed"
# Secrets file
echo "$cmd" | grep -q 'local\.properties' \
  && block "local.properties holds secrets (TMDB_TOKEN) and must not be read or changed"

exit 0
