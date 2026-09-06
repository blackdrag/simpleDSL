package dev.blackdrag.simpledsl.backend.gradle;

import dev.blackdrag.simpledsl.backend.BuildBackend;
import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.DependencyScope;
import dev.blackdrag.simpledsl.model.ExternalDependency;
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
        write(outputDirectory.resolve("build.gradle"), "allprojects { group = 'dev.blackdrag.generated'; version = '1.0-SNAPSHOT' }\n");
        for (Module module : model.modules().values()) {
            Path dir = outputDirectory.resolve(module.name());
            Files.createDirectories(dir);
            write(dir.resolve("build.gradle"), moduleBuild(module, model));
        }
    }

    private static String moduleBuild(Module module, BuildModel model) {
        String release = module.compiler() == null ? "21" : compilerRelease(module.compiler());
        StringBuilder result = new StringBuilder("plugins { id 'java' }\n\njava { toolchain { languageVersion = JavaLanguageVersion.of(" + release + ") } }\n\ndependencies {\n");
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
                result.append("    ").append(configuration).append(" '" )
                        .append(dependency.groupId()).append(":" )
                        .append(dependency.artifactId()).append(":" )
                        .append(dependency.version()).append("'\n");
            }
        }
        return result.append("}\n").toString();
    }

    private static String compilerRelease(String compiler) {
        if (compiler.startsWith("java")) return compiler.substring("java".length());
        throw new IllegalArgumentException("Unsupported compiler: " + compiler);
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }
}
