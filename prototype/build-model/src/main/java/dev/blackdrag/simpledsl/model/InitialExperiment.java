package dev.blackdrag.simpledsl.model;

public final class InitialExperiment {
    private InitialExperiment() {
    }

    public static BuildModel create() {
        BuildModel model = new BuildModel();
        model.module("core");
        model.module("generator")
                .dependsOn("core", DependencyScope.COMPILE);
        model.module("app")
                .dependsOn("core", DependencyScope.COMPILE)
                .dependsOn("generator", DependencyScope.BUILD)
                .compiler("java21")
                .testFramework("junit")
                .dependsOnExternal("org.junit.jupiter", "junit-jupiter", "5.13.4", DependencyScope.TEST);
        model.module("web")
                .dependsOn("app", DependencyScope.RUNTIME);
        model.generation("app-sources", "generator", "app",
                "dev.blackdrag.simpledsl.fixture.generator.GenerateSources",
                "generated/sources");
        return model;
    }
}
