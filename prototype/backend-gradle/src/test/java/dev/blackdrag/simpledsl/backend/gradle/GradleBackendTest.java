package dev.blackdrag.simpledsl.backend.gradle;

import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.InitialExperiment;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GradleBackendTest {
    @Test
    void generatesAllModulesAndGenerationTask() throws Exception {
        BuildModel model = InitialExperiment.create();
        Path output = Files.createTempDirectory("simpledsl-gradle-");

        new GradleBackend().generate(model, output);

        assertTrue(Files.exists(output.resolve("settings.gradle")));
        assertTrue(Files.exists(output.resolve("build.gradle")));
        assertTrue(Files.exists(output.resolve("core/build.gradle")));
        assertTrue(Files.exists(output.resolve("generator/build.gradle")));
        assertTrue(Files.exists(output.resolve("app/build.gradle")));
        assertTrue(Files.exists(output.resolve("web/build.gradle")));

        String appBuild = Files.readString(output.resolve("app/build.gradle"));
        assertTrue(appBuild.contains("generateApp_sources"));
        assertTrue(appBuild.contains("project(':generator').sourceSets.main.runtimeClasspath"));
        assertTrue(appBuild.contains("dependsOn project(':generator').tasks.named('classes')"));
        assertTrue(appBuild.contains("tasks.named('compileJava') { dependsOn 'generateApp_sources' }"));
        assertTrue(appBuild.contains("sourceSets.main.java.srcDir('generated/sources')"));
    }
}
