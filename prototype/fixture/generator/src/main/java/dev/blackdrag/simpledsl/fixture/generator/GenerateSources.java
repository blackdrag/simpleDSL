package dev.blackdrag.simpledsl.fixture.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GenerateSources {
    private GenerateSources() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("expected generated-source directory");
        }
        Path root = Path.of(args[0]);
        Path source = root.resolve("dev/blackdrag/simpledsl/fixture/generated/GeneratedValue.java");
        Files.createDirectories(source.getParent());
        Files.writeString(source, "package dev.blackdrag.simpledsl.fixture.generated;\n\n"
                + "public final class GeneratedValue {\n"
                + "    private GeneratedValue() {}\n"
                + "    public static String value() { return \"generated\"; }\n"
                + "}\n");
    }
}
