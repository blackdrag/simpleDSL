# Simplified Build DSL

A prototype for investigating whether a build model can be implemented faithfully by both Maven and Gradle without exposing backend-specific concepts in the DSL.

## Current goal

The first experiment focuses on JVM builds and deliberately exercises the areas where Maven and Gradle differ:

- multi-module projects
- compile, runtime, test, and build-time dependencies
- generated sources
- compiler/JDK selection
- test execution
- produced artifacts

The repository is an experiment first. The model and DSL syntax are expected to change as the backend comparison reveals limitations or better abstractions.

## Prototype model

The initial semantic model is intentionally backend-neutral. It describes modules, dependencies, generation, compilation, tests, and artifacts rather than Maven phases or Gradle tasks.

See `docs/build-model.md` and `docs/backend-compatibility.md` for the current assumptions and test matrix.

## Example topology

```text
core
  ^
generator ----> app ----> web
                  |
                 test
```

The generator produces source consumed by `app`, while `app` has separate compile/runtime/test/build relationships.

## Direction

The longer-term goal is not to create a thin Maven/Gradle syntax wrapper. The project may eventually model non-JVM build actions such as frontend builds and container image creation. The backend experiment is intended to determine whether that abstraction is practical.