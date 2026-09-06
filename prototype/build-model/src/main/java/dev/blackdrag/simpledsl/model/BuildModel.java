package dev.blackdrag.simpledsl.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class BuildModel {
    private final Map<String, Module> modules = new LinkedHashMap<>();
    private final List<Generation> generations = new ArrayList<>();

    public Module module(String name) {
        Objects.requireNonNull(name, "name");
        return modules.computeIfAbsent(name, Module::new);
    }

    public BuildModel generation(String name, String generatorModule, String targetModule,
                                 String mainClass, String outputDirectory) {
        generations.add(new Generation(name, generatorModule, targetModule, mainClass, outputDirectory));
        return this;
    }

    public Map<String, Module> modules() {
        return Map.copyOf(modules);
    }

    public List<Generation> generations() {
        return List.copyOf(generations);
    }
}
