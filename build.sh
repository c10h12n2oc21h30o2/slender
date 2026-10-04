#!/usr/bin/env bash
# Compila el plugin, ejecuta los tests y genera build/Slender.jar.
# Uso: ./build.sh            (compila + tests + jar)
#      ./build.sh --no-tests (compila + jar)
set -euo pipefail
cd "$(dirname "$0")"
unset JAVA_TOOL_OPTIONS || true
JAVA_HOME="${SLENDER_JDK:-/usr/lib/jvm/java-25-openjdk-amd64}"
JAVAC="$JAVA_HOME/bin/javac"; JAR="$JAVA_HOME/bin/jar"; JAVA="$JAVA_HOME/bin/java"
JUNIT="${JUNIT:-/usr/share/java/junit-platform-console-standalone.jar}"
CP="$(ls libs/*.jar | tr '\n' ':')"
rm -rf build && mkdir -p build/classes build/test-classes
find src/main/java -name '*.java' > build/sources.txt 2>/dev/null || true
if [ -s build/sources.txt ]; then
  "$JAVAC" --release 25 -encoding UTF-8 -nowarn -d build/classes -cp "$CP" @build/sources.txt
fi
if [ -d src/main/resources ]; then cp -r src/main/resources/. build/classes/; fi
if [ "${1:-}" != "--no-tests" ] && [ -d src/test/java ]; then
  find src/test/java -name '*.java' > build/test-sources.txt
  "$JAVAC" --release 25 -encoding UTF-8 -nowarn -d build/test-classes -cp "build/classes:$CP:$JUNIT" @build/test-sources.txt
  "$JAVA" -jar "$JUNIT" --disable-banner --details=summary --fail-if-no-tests \
    --class-path "build/test-classes:build/classes:$CP" --scan-class-path build/test-classes
fi
"$JAR" --create --file build/Slender.jar -C build/classes .
echo "OK -> build/Slender.jar"
