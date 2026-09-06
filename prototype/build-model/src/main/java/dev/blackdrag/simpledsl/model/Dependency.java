package dev.blackdrag.simpledsl.model;

import java.util.Objects;

public record Dependency(String target, DependencyScope scope) {
    public Dependency {
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(scope, "scope");
    }
}
