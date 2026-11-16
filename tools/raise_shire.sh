#!/usr/bin/env bash
#
# Raises the Shire's village on a fresh level with nobody online, and reports what it cost in ticks.
#
# The village's pass begins when a player is near it, so a dedicated server with nobody on it raises nothing - which is
# why the ground stage has only ever been measured by watching a client. This turns the config's own switch on, boots a
# level of its own, waits for the line that says the village stands, prints the numbers, stops the server and puts the
# settings back the way they were.
#
# Usage: tools/raise_shire.sh [level-name]        (default: shire-raise)
#
# The level is deleted before the run, because a village raised in a world that already holds it measures nothing; it is
# left behind afterwards so it can be walked to, and the log is run/raise-<level>.log.
set -uo pipefail

cd "$(dirname "$0")/.." || exit 1

LEVEL="${1:-shire-raise}"
PROPS="run/server.properties"
CONFIG="run/config/tolkienmobs-common.toml"
LOG="run/raise-$LEVEL.log"
PID=""
PROPS_BAK=""
CONFIG_BAK=""

restore() {
    if [ -n "$PROPS_BAK" ] && [ -f "$PROPS_BAK" ]; then cp "$PROPS_BAK" "$PROPS"; rm -f "$PROPS_BAK"; fi
    if [ -n "$CONFIG_BAK" ] && [ -f "$CONFIG_BAK" ]; then cp "$CONFIG_BAK" "$CONFIG"; rm -f "$CONFIG_BAK"; fi
}

stop() {
    [ -n "$PID" ] || return 0
    kill -0 "$PID" 2>/dev/null || return 0
    kill -TERM "$PID" 2>/dev/null
    for _ in $(seq 1 60); do
        kill -0 "$PID" 2>/dev/null || return 0
        sleep 1
    done
    kill -9 "$PID" 2>/dev/null
}

trap 'stop; restore' EXIT INT TERM

# A level to raise the village in, and a log beside it.
rm -rf "run/$LEVEL" "$LOG"
[ -f "$PROPS" ] && cp "$PROPS" "$PROPS.shire-bak" && PROPS_BAK="$PROPS.shire-bak"
[ -f "$CONFIG" ] && cp "$CONFIG" "$CONFIG.shire-bak" && CONFIG_BAK="$CONFIG.shire-bak"
[ -f "$PROPS" ] || : > "$PROPS"

sed -i -E "s/^level-name=.*/level-name=$LEVEL/" "$PROPS"
grep -q "^level-name=" "$PROPS" || printf 'level-name=%s\n' "$LEVEL" >> "$PROPS"
sed -i -E "s/^pause-when-empty-seconds=.*/pause-when-empty-seconds=0/" "$PROPS"
grep -q "^pause-when-empty-seconds=" "$PROPS" || printf 'pause-when-empty-seconds=0\n' >> "$PROPS"
printf 'eula=true\n' > run/eula.txt

# The switch the pass reads at start, in the mod's own config, said plainly enough to survive a file that is not there.
python - "$CONFIG" <<'PY'
import pathlib
import re
import sys

path = pathlib.Path(sys.argv[1])
text = path.read_text() if path.exists() else ""
if re.search(r"^\s*raiseShireOnStart\s*=", text, re.M):
    text = re.sub(r"^(\s*raiseShireOnStart\s*=).*$", r"\1 true", text, flags=re.M)
elif re.search(r"^\s*\[server\.gameplay\]\s*$", text, re.M):
    text = re.sub(r"(^\s*\[server\.gameplay\]\s*$)", r"\1\n\t\traiseShireOnStart = true", text,
                  count=1, flags=re.M)
else:
    text = text.rstrip("\n") + "\n[server.gameplay]\n\t\traiseShireOnStart = true\n"
path.parent.mkdir(parents=True, exist_ok=True)
path.write_text(text)
PY

echo "== $LEVEL: booting a server with nobody online; the log is $LOG"
./gradlew runServer --console=plain > "$LOG" 2>&1 &
PID=$!

STOOD=""
DEADLINE=$((SECONDS + 1200))
while [ $SECONDS -lt $DEADLINE ]; do
    if grep -q "the village stands at" "$LOG" 2>/dev/null; then
        STOOD=$(grep -m1 "the village stands at" "$LOG")
        break
    fi
    if ! kill -0 "$PID" 2>/dev/null; then break; fi
    sleep 5
done

echo
grep -m1 "raising the village" "$LOG" 2>/dev/null
if [ -n "$STOOD" ]; then
    echo "$STOOD"
    echo
    echo "== it stood: $(grep -c 'the village stands at' "$LOG") completion line(s)"
    exit 0
fi

echo "== no village: the server stopped or the pass never began. The last of $LOG:"
tail -n 40 "$LOG"
exit 1
