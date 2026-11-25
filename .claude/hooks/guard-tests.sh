#!/bin/bash
# PostToolUse hook (Edit|Write): after a test file is edited, reject silenced tests.
# The edit already happened; exit 2 shows stderr to Claude so it fixes it next.
f=$(python3 -c 'import json,sys; print(json.load(sys.stdin).get("tool_input",{}).get("file_path",""))')

case "$f" in
  *Test.kt)
    if grep -nE '@Ignore|@Disabled' "$f" >&2; then
      echo "guard-tests: $f now contains @Ignore/@Disabled (lines above). Don't silence tests: revert that, then fix the code or explain to the user why the test is wrong." >&2
      exit 2
    fi
    ;;
esac
exit 0
