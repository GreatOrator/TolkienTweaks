#!/usr/bin/env bash
# Runs the replacement model generator (com.greatorator.tolkienmobs.datagen.modelgen) on a spread
# of vanilla blocks and compares every file it writes with vanilla's own, straight out of the client
# jar. This needs no mod classes, so it works while the rest of the 1.21.4 port does not compile.
#
# Usage: tools/modelgen-verify/run.sh
#   JAVA_HOME  - a JDK 21 (defaults to the Gradle-provisioned one)
#   WORK       - scratch directory (defaults to a temp dir)
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/../.." && pwd)
WORK=${WORK:-$(mktemp -d)}
MC_VERSION=$(sed -n 's/^minecraft_version=//p' "$ROOT/gradle.properties" | tr -d '\r')
GRADLE_PROPERTIES="$HOME/.gradle/gradle.properties"

if [ ! -x "${JAVA_HOME:-}/bin/javac" ]; then
    # JAVA_HOME here is often a JRE, and the game needs 21 anyway.
    JAVA_HOME=$(ls -d "$HOME"/.gradle/jdks/eclipse_adoptium-21*/ 2>/dev/null | head -1)
fi
if [ ! -x "${JAVA_HOME:-}/bin/javac" ]; then
    echo "Set JAVA_HOME to a JDK 21" >&2
    exit 1
fi
JAVA="$JAVA_HOME/bin/java"
JAVAC="$JAVA_HOME/bin/javac"

CLIENT_JAR=$(ls "$HOME"/.gradle/caches/neoformruntime/artifacts/minecraft_"$MC_VERSION"_client.jar 2>/dev/null | head -1)
if [ -z "$CLIENT_JAR" ]; then
    echo "No client jar for $MC_VERSION; run a Gradle task once so it gets provisioned" >&2
    exit 1
fi

echo "== compile classpath"
mkdir -p "$WORK"
cat > "$WORK/classpath.gradle" <<'EOF'
allprojects {
    tasks.register('dumpCompileClasspath') {
        doLast {
            def ss = project.extensions.findByName('sourceSets')
            if (ss?.findByName('main')) println "CP=" + ss.main.compileClasspath.asPath
        }
    }
}
EOF
(cd "$ROOT" && ./gradlew -I "$WORK/classpath.gradle" dumpCompileClasspath -q --offline \
    -Dorg.gradle.jvmargs="-Xmx4G" 2>/dev/null | grep '^CP=' | sed 's/^CP=//') > "$WORK/cp.txt"

echo "== build harness"
mkdir -p "$WORK/src/com/greatorator/tolkienmobs/datagen" "$WORK/classes" "$WORK/out"
cp -r "$ROOT/src/main/java/com/greatorator/tolkienmobs/datagen/modelgen" "$WORK/src/com/greatorator/tolkienmobs/datagen/"
cp -r "$ROOT/tools/modelgen-verify/src/verify" "$WORK/src/"
"$JAVAC" -nowarn -proc:none -d "$WORK/classes" -cp "$(cat "$WORK/cp.txt")" \
    $(find "$WORK/src" -name '*.java')

case "$(uname -s)" in
    MINGW* | MSYS* | CYGWIN*) SEP=';' ;;
    *) SEP=':' ;;
esac

# The JVM wants Windows paths on Windows, even though bash hands us /c/...
if [ "$SEP" = ';' ]; then
    WORK=$(cd "$WORK" && pwd -W)
    CLIENT_JAR=$(cygpath -w "$CLIENT_JAR")
fi

echo "== generate and compare"
"$JAVA" -cp "$WORK/classes$SEP$(cat "$WORK/cp.txt")$SEP$CLIENT_JAR" verify.VerifyModelGen \
    "$WORK/out" "$CLIENT_JAR"
