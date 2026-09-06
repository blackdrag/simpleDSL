package dev.blackdrag.simpledsl.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BuildModelTest {
    @Test
    void describesTheInitialBackendExperiment() {
        BuildModel model = new BuildModel();

        model.module("core");
        model.module("generator")
                .dependsOn("core", DependencyScope.COMPILE)
                .generator("app");
        model.module("app")
                .dependsOn("core", DependencyScope.COMPILE)
                .dependsOn("generator", DependencyScope.BUILD)
                .dependsOnExternal("org.junit.jupiter", "junit-jupiter", "5.13.4", DependencyScope.TEST)
                .compiler("java21")
                .testFramework("junit");
        model.module("web")
                .dependsOn("app", DependencyScope.RUNTIME);

        assertEquals(4, model.modules().size());
        assertEquals(2, model.module("app").dependencies().size());
        assertEquals(1, model.module("app").externalDependencies().size());
        assertTrue(model.module("app").dependencies().stream()
                .anyMatch(d -> d.scope() == DependencyScope.BUILD && d.target().equals("generator")));
        assertEquals(DependencyScope.TEST, model.module("app").externalDependencies().getFirst().scope());
    }
}
