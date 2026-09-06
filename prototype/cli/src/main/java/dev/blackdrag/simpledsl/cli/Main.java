package dev.blackdrag.simpledsl.cli;

import dev.blackdrag.simpledsl.backend.BuildBackend;
import dev.blackdrag.simpledsl.backend.gradle.GradleBackend;
import dev.blackdrag.simpledsl.backend.maven.MavenBackend;
import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.InitialExperiment;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("usage: Main <maven|gradle> <output-directory>");
        }
        BuildModel model = InitialExperiment.create();
        BuildBackend backend = switch (args[0]) {
            case "maven" -> new MavenBackend();
            case "gradle" -> new GradleBackend();
            default -> throw new IllegalArgumentException("unknown backend: " + args[0]);
        };
        Path output = Path.of(args[1]);
        Files.createDirectories(output);
        backend.generate(model, output);
    }
}
