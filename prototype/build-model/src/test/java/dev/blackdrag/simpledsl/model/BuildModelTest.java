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
                .dependsOn("junit", DependencyScope.TEST)
                .compiler("java21")
                .testFramework("junit");
        model.module("web")
                .dependsOn("app", DependencyScope.RUNTIME);

        assertEquals(4, model.modules().size());
        assertEquals(4, model.module("app").dependencies().size());
        assertTrue(model.module("app").dependencies().stream()
                .anyMatch(d -> d.scope() == DependencyScope.BUILD && d.target().equals("generator")));
    }
}
