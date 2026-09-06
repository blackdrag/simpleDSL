package dev.blackdrag.simpledsl.backend;

import dev.blackdrag.simpledsl.model.BuildModel;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BackendContractTest {
    @Test
    void backendCanGenerateAnEmptyProject() throws Exception {
        BuildBackend backend = (model, output) -> Files.createDirectories(output);

        assertDoesNotThrow(() -> backend.generate(new BuildModel(),
                Files.createTempDirectory("simpledsl-backend-test")));
    }
}
