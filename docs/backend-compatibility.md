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
| Transitive dependencies | pending | pending | Native resolution is an input to the DSL's dependency analysis |
| Generated sources | pending | pending | Ordering is significant |
| Compiler/JDK selection | pending | pending | Keep semantic model backend-neutral |
| Test execution | pending | pending | |
| Artifact packaging | pending | pending | |
| Clean build | pending | pending | |
| Incremental build | pending | pending | Important for later experiments |

## Method

For each capability, construct one build model and have each backend realize it using its native build mechanisms. A generated build description is one possible backend implementation, but it is not itself the semantic model.

Execute each backend and compare observable semantics rather than generated file text. Relevant observations include:

- whether compilation succeeds
- which classes are visible on each classpath
- whether generated sources are available at the required point
- which artifacts are produced
- whether test-only and build-only dependencies leak into runtime
- how the native dependency resolver resolves conflicts and transitive dependencies
- whether the resulting dependency information can be made explicit and reproducible
- whether clean and incremental builds behave consistently

## Dependency-resolution direction

The goal is a backend-independent dependency declaration, not a new universal dependency-resolution algorithm.

The backend's native resolver can be used to discover the transitive dependency graph. Simplified Build DSL can then analyze that result against the declarations in the DSL. A dependency declared with an exact version is an exact requirement; conflicting exact requirements are errors rather than something the backend should silently choose between.

Dependencies introduced only transitively are initially treated as exact at the version selected by the native resolver. This allows the DSL to turn implicit dependency choices into explicit user-controlled declarations without having to reproduce the entire dependency-resolution algorithm of every ecosystem.

The desired workflow is therefore:

```text
DSL declarations
      |
      v
native backend resolution
      |
      v
resolved graph
      |
      v
DSL conflict / reproducibility analysis
      |
      v
suggestions to the user
```

The user decides whether to change the DSL. A future lock representation can preserve a concrete resolution without making the native resolver's conflict policy the DSL's semantic policy.

## What-if dependency checks

The tool should also support a non-mutating dependency check. Given a dependency and a candidate version, the backend should perform a hypothetical resolution using that version, and the tool should report the consequences.

For example:

```text
check foo = 3.2.0
```

can report:

- which resolved versions would change
- which declared requirements would conflict
- which declarations would likely need to change for a passing build
- what the current backend-native resolution would select

A check for a newest available candidate is also an analysis operation, not a DSL dependency semantic. The DSL should not gain an implicit `latest` meaning merely because a user asked the tool to investigate a newest version.

## Rule

A backend-specific workaround is acceptable inside a backend. It is a problem only when the workaround requires a backend-specific concept to leak into the model or changes the intended semantics.

Backend-specific dependency features are not automatically required to become core DSL concepts. Examples include Gradle variants and capabilities and Maven classifiers, relocations, optional dependencies, exclusions, and dependency-management mechanisms. They should only be normalized when a useful backend-independent meaning is established.
