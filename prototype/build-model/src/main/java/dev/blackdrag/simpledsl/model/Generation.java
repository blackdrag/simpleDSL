package dev.blackdrag.simpledsl.model;

import java.util.Objects;

public record Generation(String name, String generatorModule, String targetModule, String mainClass, String outputDirectory) {
    public Generation {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(generatorModule, "generatorModule");
        Objects.requireNonNull(targetModule, "targetModule");
        Objects.requireNonNull(mainClass, "mainClass");
        Objects.requireNonNull(outputDirectory, "outputDirectory");
    }
}
