package dev.blackdrag.simpledsl.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Module {
    private final String name;
    private final List<Dependency> dependencies = new ArrayList<>();
    private String compiler;
    private String generator;
    private String testFramework;

    public Module(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String name() { return name; }
    public List<Dependency> dependencies() { return List.copyOf(dependencies); }
    public String compiler() { return compiler; }
    public String generator() { return generator; }
    public String testFramework() { return testFramework; }

    public Module dependsOn(String target, DependencyScope scope) {
        dependencies.add(new Dependency(target, scope));
        return this;
    }

    public Module compiler(String compiler) {
        this.compiler = compiler;
        return this;
    }

    public Module generator(String generator) {
        this.generator = generator;
        return this;
    }

    public Module testFramework(String testFramework) {
        this.testFramework = testFramework;
        return this;
    }
}
