package dev.blackdrag.simpledsl.backend.maven;

import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.InitialExperiment;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MavenBackendTest {
    @Test
    void generatesAllModulesAndGenerationConfiguration() throws Exception {
        BuildModel model = InitialExperiment.create();
        Path output = Files.createTempDirectory("simpledsl-maven-");

        new MavenBackend().generate(model, output);

        assertTrue(Files.exists(output.resolve("pom.xml")));
        assertTrue(Files.exists(output.resolve("core/pom.xml")));
        assertTrue(Files.exists(output.resolve("generator/pom.xml")));
        assertTrue(Files.exists(output.resolve("app/pom.xml")));
        assertTrue(Files.exists(output.resolve("web/pom.xml")));

        String appPom = Files.readString(output.resolve("app/pom.xml"));
        assertTrue(appPom.contains("<artifactId>exec-maven-plugin</artifactId>"));
        assertTrue(appPom.contains("<phase>generate-sources</phase>"));
        assertTrue(appPom.contains("<mainClass>dev.blackdrag.simpledsl.fixture.generator.GenerateSources</mainClass>"));
        assertTrue(appPom.contains("../generator/target/classes"));
        assertTrue(appPom.contains("<artifactId>build-helper-maven-plugin</artifactId>"));
        assertTrue(appPom.contains("<source>${project.basedir}/generated/sources</source>"));
    }
}
