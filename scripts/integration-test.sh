#!/bin/sh
set -eu

ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
CLI_CP="$ROOT/prototype/cli/target/classes:$ROOT/prototype/build-model/target/classes:$ROOT/prototype/backend-api/target/classes:$ROOT/prototype/backend-maven/target/classes:$ROOT/prototype/backend-gradle/target/classes"

rm -rf "$ROOT/target/integration"
mkdir -p "$ROOT/target/integration"

java -cp "$CLI_CP" dev.blackdrag.simpledsl.cli.Main maven "$ROOT/target/integration/maven"
java -cp "$CLI_CP" dev.blackdrag.simpledsl.cli.Main gradle "$ROOT/target/integration/gradle"

for backend in maven gradle; do
  cp -R "$ROOT/prototype/fixture/." "$ROOT/target/integration/$backend/fixture"
done

cp -R "$ROOT/prototype/fixture/core/." "$ROOT/target/integration/maven/core/"
cp -R "$ROOT/prototype/fixture/generator/." "$ROOT/target/integration/maven/generator/"
cp -R "$ROOT/prototype/fixture/app/." "$ROOT/target/integration/maven/app/"
cp -R "$ROOT/prototype/fixture/web/." "$ROOT/target/integration/maven/web/"
cp -R "$ROOT/prototype/fixture/core/." "$ROOT/target/integration/gradle/core/"
cp -R "$ROOT/prototype/fixture/generator/." "$ROOT/target/integration/gradle/generator/"
cp -R "$ROOT/prototype/fixture/app/." "$ROOT/target/integration/gradle/app/"
cp -R "$ROOT/prototype/fixture/web/." "$ROOT/target/integration/gradle/web/"

mvn -q -f "$ROOT/target/integration/maven/pom.xml" test

if command -v gradle >/dev/null 2>&1; then
  gradle -q -p "$ROOT/target/integration/gradle" test
else
  echo "gradle executable not found; generated Gradle build was not executed" >&2
  exit 1
fi
