package dev.blackdrag.simpledsl.backend.maven;

import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.InitialExperiment;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenBackendTest {
    @Test
    void generatesAllModules() throws Exception {
        BuildModel model = InitialExperiment.create();
        Path output = Files.createTempDirectory("simpledsl-maven-");

        new MavenBackend().generate(model, output);

        assertTrue(Files.exists(output.resolve("pom.xml")));
        assertTrue(Files.exists(output.resolve("core/pom.xml")));
        assertTrue(Files.exists(output.resolve("generator/pom.xml")));
        assertTrue(Files.exists(output.resolve("app/pom.xml")));
        assertTrue(Files.exists(output.resolve("web/pom.xml")));
    }
}
