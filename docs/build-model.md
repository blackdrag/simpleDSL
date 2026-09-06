# Build Model

## Purpose

The model describes the semantics that a simplified build language wants to expose. A backend must realize those semantics using the native concepts and mechanisms of the selected build system.

## Initial concepts

### Module

A module is an independently buildable unit with sources, dependencies, optional generated sources, tests, and one or more artifacts.

### Dependencies

External dependencies are declared in the model rather than expressed directly in Maven, Gradle, npm, or another backend's terminology. The declaration should have a uniform meaning independent of the selected backend.

An exact version is the simplest and strongest form of declaration. For example, declaring `2.0` means that version `2.0` is required; a different version such as `2.1` is a conflict rather than a backend-defined choice.

Exact versions are treated as opaque version identifiers. They do not have to follow Semantic Versioning (SemVer). Versions such as `1.0.0.Final`, `2026.09`, or vendor-qualified versions can therefore be used as exact versions without the DSL having to interpret their structure.

For non-exact version constraints, the primary portable version model is SemVer. The DSL may define constraints that allow controlled changes to a SemVer version, such as changes within a patch or minor range. A non-exact constraint is only meaningful where the version can be interpreted according to the DSL's SemVer rules; arbitrary ecosystem-specific version strings remain exact-only unless a backend-native form is explicitly requested.

A backend-native form may be useful when the user deliberately wants the dependency system of the selected backend to make the decision. This should be explicit (for example, `native`) rather than implying a vague meaning such as `latest`. `latest`, if supported at all, is a tool or backend capability rather than a universal DSL promise about which version will be selected.

### Dependency resolution

The project aims for backend-independent dependency declarations, not a universal replacement for Maven, Gradle, npm, or other dependency resolvers.

The general tool is responsible for interpreting the dependency requirements expressed by the DSL. For SemVer requirements, it collects the constraints imposed on a dependency and calculates their intersection. This gives one semantic requirement that the selected backend is expected to satisfy.

For example:

```text
A
├── B
│   └── foo:^1.2
└── C
    └── foo:^1.3
```

The intersection of the requirements is `^1.3`. If the intersection is empty, the DSL requirements are contradictory and the tool should report this as a DSL error, explaining which requirements caused the contradiction and why they cannot be satisfied together.

If the intersection is non-empty, the backend is responsible for resolving a concrete version that satisfies it. The backend may use its native dependency resolver rather than reproducing Maven, Gradle, npm, or another ecosystem's resolution algorithm.

There are therefore several distinct outcomes:

- **Empty intersection:** the DSL contains mutually incompatible requirements. This is a DSL error.
- **Non-empty intersection, but no available dependency satisfies it:** the user's requirement is logically valid, but dependency resolution cannot fulfill it. This is a dependency resolution error.
- **The backend resolves a version inside the intersection:** the resolution satisfies the DSL requirements.
- **The backend resolves a version outside the intersection:** the backend did not honor the DSL requirement. The tool must report the violation even if the reason is unknown. The backend may provide a backend-specific explanation or workaround.

The backend is responsible for the parts that depend on its native capabilities: translating the DSL requirement, performing resolution, inspecting the resolved graph, detecting when the result violates the requested constraint, and advising the user about backend-specific limitations or configuration. The core DSL should not acquire backend-specific concepts merely to work around such a limitation.

The diagnostic should distinguish the semantic expectation from the backend result. For example, if the DSL requires `^1.3` and the backend selects `2.0.0`, the tool should be able to state that `^1.3` was the expected requirement and that `2.0.0` does not satisfy it. If the backend cannot faithfully represent or enforce the requested range, it may advise the user to use an exact version or another backend-specific solution.

Transitive dependencies do not need to become explicit DSL declarations merely because they are transitive. If all dependency paths impose the same version requirement or the resulting requirement has a single unambiguous resolution, no additional declaration is needed. Once different transitive requirements require a version decision, that decision must be represented explicitly by the user in the DSL rather than silently depending on a backend's conflict-resolution policy.

For example:

```text
A
├── B
│   └── foo:^1.2
└── C
    └── foo:^1.3
```

requires the effective requirement `^1.3`, but does not require another declaration if that requirement is already represented by the DSL. In contrast, incompatible requirements such as `^1.2` and `^2.0` have an empty intersection and must be reported as contradictory requirements.

Dependency resolution is therefore expected to be a feedback loop:

```text
DSL declarations
      |
      v
constraint intersection
      |
      v
backend-native resolution
      |
      v
resolved dependency graph
      |
      v
validation against DSL requirements
      |
      v
user-visible diagnostics / suggestions
      |
      v
user edits the DSL
```

The user remains in control of changing the DSL. Backend-specific advice may explain how to make a valid DSL requirement work with that backend, but such advice does not become part of the backend-independent model.

A future lock representation may record the concrete resolved result, while the DSL remains the source of user intent. Locking is a backend-specific implementation detail and may use either a tool-generated lock or the native lock mechanism of the selected backend.

### What-if dependency checks

The tool should support a non-mutating operation that asks what would happen if an external dependency were changed to a particular version. This is not intended to be an automatic upgrade command.

For example:

```text
check foo = 3.2.0
```

should perform a hypothetical backend resolution and report the consequences: changed transitive versions, conflicts with other declared requirements, and DSL declarations that would likely need to change for a successful build.

A similar check may determine a candidate such as the newest available version, but the resulting candidate should be presented as a suggestion. It does not become a dynamic dependency declaration merely because the check requested the newest version.

### Dependency relationships

The prototype currently uses four semantic relationships:

- `compile`: required to compile the consumer and available to its normal runtime/test classpaths.
- `runtime`: not required to compile the consumer, but required when running or packaging it.
- `test`: available only to test compilation and test execution.
- `build`: required to perform a build action such as source generation; it is not an application runtime dependency.

These names are semantic placeholders. The experiment must determine whether they are sufficient and whether some currently bundled consequences should instead be modeled separately.

### Generation

A generator consumes build-time inputs and produces source or another build artifact that becomes an input of a later action. The model should express the dependency between those actions without requiring users to know Maven lifecycle phases or Gradle task names.

### Compiler

Compilation has a compiler/JDK selection independent of the backend. A future model may distinguish toolchain selection from compiler implementation/provider.

### Test

A module may declare a test framework and test dependencies. The backend is responsible for configuring test compilation and execution.

## Important design constraints

The model must not contain Maven phases, Maven scopes, Gradle configurations, Gradle task names, or equivalent backend-specific concepts.

At the same time, backend independence does not mean that every backend-specific dependency feature must be normalized into the core model. Features such as Gradle variants or Maven classifiers should only become shared model concepts when there is a clear backend-independent meaning worth standardizing.

The backend is responsible for using its native build and dependency mechanisms where that is necessary to realize the model.

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
  compile -> app
```

The exact fixture will evolve as semantic gaps are discovered.
