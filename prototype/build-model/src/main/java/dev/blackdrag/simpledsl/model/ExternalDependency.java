package dev.blackdrag.simpledsl.model;

import java.util.Objects;

public record ExternalDependency(String groupId, String artifactId, String version, DependencyScope scope) {
    public ExternalDependency {
        Objects.requireNonNull(groupId, "groupId");
        Objects.requireNonNull(artifactId, "artifactId");
        Objects.requireNonNull(version, "version");
        Objects.requireNonNull(scope, "scope");
    }
}
