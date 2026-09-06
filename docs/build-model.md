# Build Model

## Purpose

The model describes the semantics that a simplified build language wants to expose. A backend must translate those semantics into the native concepts of the selected build system.

## Initial concepts

### Module

A module is an independently buildable unit with sources, dependencies, optional generated sources, tests, and one or more artifacts.

### Dependency scopes

The prototype deliberately uses four semantic relationships:

- `compile`: required to compile the consumer and available to its normal runtime/test classpaths.
- `runtime`: not required to compile the consumer, but required when running or packaging it.
- `test`: available only to test compilation and test execution.
- `build`: required to perform a build action such as source generation; it is not an application runtime dependency.

These names are semantic placeholders. The experiment must determine whether they are sufficient.

### Generation

A generator consumes build-time inputs and produces source or another build artifact that becomes an input of a later action. The model should express the dependency between those actions without requiring users to know Maven lifecycle phases or Gradle task names.

### Compiler

Compilation has a compiler/JDK selection independent of the backend. A future model may distinguish toolchain selection from compiler implementation/provider.

### Test

A module may declare a test framework and test dependencies. The backend is responsible for configuring test compilation and execution.

## Important design constraint

The model must not contain Maven phases, Maven scopes, Gradle configurations, Gradle task names, or equivalent backend-specific concepts. Backend-specific information belongs in the backend implementation.

## Initial experiment

The first fixture should contain:

```text
core
  no external dependencies

generator
  compile -> core
  generates sources for app

app
  compile -> core
  build -> generator
  test -> JUnit

web
  runtime -> app
```

The exact fixture will evolve as semantic gaps are discovered.