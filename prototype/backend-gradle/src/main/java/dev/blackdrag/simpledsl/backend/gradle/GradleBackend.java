package dev.blackdrag.simpledsl.backend.gradle;

import dev.blackdrag.simpledsl.backend.BuildBackend;
import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.DependencyScope;
import dev.blackdrag.simpledsl.model.ExternalDependency;
import dev.blackdrag.simpledsl.model.Generation;
import dev.blackdrag.simpledsl.model.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GradleBackend implements BuildBackend {
    @Override
    public void generate(BuildModel model, Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);
        StringBuilder settings = new StringBuilder("rootProject.name = 'generated-build'\n");
        for (Module module : model.modules().values()) {
            settings.append("include ':").append(module.name()).append("'\n");
        }
        write(outputDirectory.resolve("settings.gradle"), settings.toString());
        write(outputDirectory.resolve("build.gradle"), "allprojects { group = 'dev.blackdrag.generated'; version = '1.0-SNAPSHOT' }\n\nallprojects { repositories { mavenCentral() } }\n");
        for (Module module : model.modules().values()) {
            Path dir = outputDirectory.resolve(module.name());
            Files.createDirectories(dir);
            write(dir.resolve("build.gradle"), moduleBuild(module, model));
        }
    }

    private static String moduleBuild(Module module, BuildModel model) {
        String release = module.compiler() == null ? "21" : compilerRelease(module.compiler());
        StringBuilder result = new StringBuilder("plugins { id 'java' }\n\njava { toolchain { languageVersion = JavaLanguageVersion.of(" + release + ") } }\n\n");
        appendGenerationConfiguration(result, module, model);
        appendTestConfiguration(result, module);
        result.append("dependencies {\n");
        module.dependencies().stream()
                .filter(d -> model.modules().containsKey(d.target()))
                .forEach(d -> {
                    String configuration = switch (d.scope()) {
                        case COMPILE -> "implementation";
                        case RUNTIME -> "runtimeOnly";
                        case TEST -> "testImplementation";
                        case BUILD -> null;
                    };
                    if (configuration != null) {
                        result.append("    ").append(configuration).append(" project(':").append(d.target()).append("')\n");
                    }
                });
        for (ExternalDependency dependency : module.externalDependencies()) {
            String configuration = switch (dependency.scope()) {
                case COMPILE -> "implementation";
                case RUNTIME -> "runtimeOnly";
                case TEST -> "testImplementation";
                case BUILD -> null;
            };
            if (configuration != null) {
                result.append("    ").append(configuration).append(" '")
                        .append(dependency.groupId()).append(":")
                        .append(dependency.artifactId()).append(":")
                        .append(dependency.version()).append("'\n");
            }
        }
        if ("junit".equals(module.testFramework())) {
            result.append("    testRuntimeOnly 'org.junit.platform:junit-platform-launcher:1.13.4'\n");
        }
        return result.append("}\n").toString();
    }

    private static void appendTestConfiguration(StringBuilder result, Module module) {
        if ("junit".equals(module.testFramework())) {
            result.append("tasks.named('test') { useJUnitPlatform() }\n\n");
        }
    }

    private static void appendGenerationConfiguration(StringBuilder result, Module module, BuildModel model) {
        for (Generation generation : model.generations()) {
            if (!generation.targetModule().equals(module.name())) {
                continue;
            }
            String taskName = "generate" + capitalize(generation.name());
            String output = generation.outputDirectory();
            result.append("sourceSets.main.java.srcDir('").append(output).append("')\n\n")
                    .append("tasks.register('").append(taskName).append("', JavaExec) {\n")
                    .append("    classpath = project(':").append(generation.generatorModule()).append("').sourceSets.main.runtimeClasspath\n")
                    .append("    mainClass = '").append(generation.mainClass()).append("'\n")
                    .append("    args file('").append(output).append("')\n")
                    .append("    dependsOn project(':").append(generation.generatorModule()).append("').tasks.named('classes')\n")
                    .append("    outputs.dir file('").append(output).append("')\n")
                    .append("}\n")
                    .append("tasks.named('compileJava') { dependsOn '").append(taskName).append("' }\n\n");
        }
    }

    private static String capitalize(String value) {
        if (value.isEmpty()) return value;
        return Character.toUpperCase(value.charAt(0)) + value.substring(1).replace('-', '_');
    }

    private static String compilerRelease(String compiler) {
        if (compiler.startsWith("java")) return compiler.substring("java".length());
        throw new IllegalArgumentException("Unsupported compiler: " + compiler);
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }
}
