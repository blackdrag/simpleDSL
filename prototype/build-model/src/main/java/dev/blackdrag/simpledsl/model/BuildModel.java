package dev.blackdrag.simpledsl.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class BuildModel {
    private final Map<String, Module> modules = new LinkedHashMap<>();

    public Module module(String name) {
        Objects.requireNonNull(name, "name");
        return modules.computeIfAbsent(name, Module::new);
    }

    public Map<String, Module> modules() {
        return Map.copyOf(modules);
    }
}
