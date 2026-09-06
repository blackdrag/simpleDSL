package dev.blackdrag.simpledsl.backend.maven;

import dev.blackdrag.simpledsl.backend.BuildBackend;
import dev.blackdrag.simpledsl.model.BuildModel;
import dev.blackdrag.simpledsl.model.DependencyScope;
import dev.blackdrag.simpledsl.model.ExternalDependency;
import dev.blackdrag.simpledsl.model.Module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MavenBackend implements BuildBackend {
    @Override
    public void generate(BuildModel model, Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);
        write(outputDirectory.resolve("pom.xml"), rootPom(model));
        for (Module module : model.modules().values()) {
            Path dir = outputDirectory.resolve(module.name());
            Files.createDirectories(dir);
            write(dir.resolve("pom.xml"), modulePom(module, model));
        }
    }

    private static String rootPom(BuildModel model) {
        StringBuilder modules = new StringBuilder();
        for (Module module : model.modules().values()) {
            modules.append("    <module>").append(module.name()).append("</module>\n");
        }
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <groupId>dev.blackdrag.generated</groupId>
                  <artifactId>generated-build</artifactId>
                  <version>1.0-SNAPSHOT</version>
                  <packaging>pom</packaging>
                  <modules>
                """ + modules + "  </modules>\n</project>\n""";
    }

    private static String modulePom(Module module, BuildModel model) {
        StringBuilder dependencies = new StringBuilder();
        module.dependencies().stream()
                .filter(d -> d.scope() != DependencyScope.BUILD)
                .filter(d -> model.modules().containsKey(d.target()))
                .forEach(d -> appendModuleDependency(dependencies, d.target(), d.scope()));
        for (ExternalDependency dependency : module.externalDependencies()) {
            if (dependency.scope() != DependencyScope.BUILD) {
                appendExternalDependency(dependencies, dependency);
            }
        }

        String compilerRelease = module.compiler() == null ? "21" : compilerRelease(module.compiler());
        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                  <modelVersion>4.0.0</modelVersion>
                  <parent>
                    <groupId>dev.blackdrag.generated</groupId>
                    <artifactId>generated-build</artifactId>
                    <version>1.0-SNAPSHOT</version>
                  </parent>
                  <artifactId>""" + module.name() + """</artifactId>
                  <properties>
                    <maven.compiler.release>""" + compilerRelease + """</maven.compiler.release>
                  </properties>
                  <dependencies>
                """ + dependencies + "  </dependencies>\n</project>\n""";
    }

    private static void appendModuleDependency(StringBuilder result, String target, DependencyScope scope) {
        result.append("    <dependency>\n")
                .append("      <groupId>dev.blackdrag.generated</groupId>\n")
                .append("      <artifactId>").append(target).append("</artifactId>\n")
                .append("      <version>1.0-SNAPSHOT</version>\n");
        appendScope(result, scope);
        result.append("    </dependency>\n");
    }

    private static void appendExternalDependency(StringBuilder result, ExternalDependency dependency) {
        result.append("    <dependency>\n")
                .append("      <groupId>").append(dependency.groupId()).append("</groupId>\n")
                .append("      <artifactId>").append(dependency.artifactId()).append("</artifactId>\n")
                .append("      <version>").append(dependency.version()).append("</version>\n");
        appendScope(result, dependency.scope());
        result.append("    </dependency>\n");
    }

    private static void appendScope(StringBuilder result, DependencyScope scope) {
        if (scope == DependencyScope.TEST) result.append("      <scope>test</scope>\n");
        else if (scope == DependencyScope.RUNTIME) result.append("      <scope>runtime</scope>\n");
    }

    private static String compilerRelease(String compiler) {
        if (compiler.startsWith("java")) return compiler.substring("java".length());
        throw new IllegalArgumentException("Unsupported compiler: " + compiler);
    }

    private static void write(Path path, String content) throws IOException {
        Files.writeString(path, content);
    }
}
