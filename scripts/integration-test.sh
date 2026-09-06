#!/bin/sh
set -eu

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
CLI_CP="$ROOT/prototype/cli/target/classes:$ROOT/prototype/build-model/target/classes:$ROOT/prototype/backend-api/target/classes:$ROOT/prototype/backend-maven/target/classes:$ROOT/prototype/backend-gradle/target/classes"

rm -rf "$ROOT/target/integration"
mkdir -p "$ROOT/target/integration"

java -cp "$CLI_CP" dev.blackdrag.simpledsl.cli.Main maven "$ROOT/target/integration/maven"
java -cp "$CLI_CP" dev.blackdrag.simpledsl.cli.Main gradle "$ROOT/target/integration/gradle"

for backend in maven gradle; do
  for module in core generator app web; do
    mkdir -p "$ROOT/target/integration/$backend/$module"
    cp -R "$ROOT/prototype/fixture/$module/src" "$ROOT/target/integration/$backend/$module/"
  done
done

mvn -q -f "$ROOT/target/integration/maven/pom.xml" test

if command -v gradle >/dev/null 2>&1; then
  gradle -q -p "$ROOT/target/integration/gradle" test
else
  echo "gradle executable not found; generated Gradle build was not executed" >&2
  exit 1
fi
