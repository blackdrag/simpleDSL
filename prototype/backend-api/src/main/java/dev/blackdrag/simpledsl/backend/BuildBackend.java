package dev.blackdrag.simpledsl.backend;

import dev.blackdrag.simpledsl.model.BuildModel;

import java.io.IOException;
import java.nio.file.Path;

public interface BuildBackend {
    void generate(BuildModel model, Path outputDirectory) throws IOException;
}
