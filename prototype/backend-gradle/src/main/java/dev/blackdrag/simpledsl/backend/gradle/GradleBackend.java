package dev.blackdrag.simpledsl.backend.gradle;

import dev.blackdrag.simpledsl.backend.BuildBackend;
import dev.blackdrag.simpledsl.model.BuildModel;
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
        StringBuilder result = new StringBuilder("plugins { id 'java' }\n\njava { toolchain { languageVersion = JavaLanguageVersion.of(21) } }\n\ndependencies {\n");
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
        return result.append("}\n").toString();
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }
}
