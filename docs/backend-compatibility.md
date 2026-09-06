# Maven / Gradle Backend Compatibility

This document records observations from implementing the same semantic build model with different backends.

## Initial matrix

| Capability | Maven | Gradle | Notes |
|---|---|---|---|
| Modules | pending | pending | Multi-module topology |
| Compile dependency | pending | pending | |
| Runtime dependency | pending | pending | |
| Test dependency | pending | pending | |
| Build-time dependency | pending | pending | Source generation is the first case |
| Transitive dependencies | pending | pending | Must compare effective classpaths |
| Generated sources | pending | pending | Ordering is significant |
| Compiler/JDK selection | pending | pending | Keep semantic model backend-neutral |
| Test execution | pending | pending | |
| Artifact packaging | pending | pending | |
| Clean build | pending | pending | |
| Incremental build | pending | pending | Important for later experiments |

## Method

For each capability, construct one build model and have each backend generate its native build description. Execute both builds and compare observable semantics rather than generated file text.

Relevant observations include:

- whether compilation succeeds
- which classes are visible on each classpath
- whether generated sources are available at the required point
- which artifacts are produced
- whether test-only and build-only dependencies leak into runtime
- whether clean and incremental builds behave consistently

## Rule

A backend-specific workaround is acceptable inside a backend. It is a problem only when the workaround requires a backend-specific concept to leak into the model or changes the intended semantics.