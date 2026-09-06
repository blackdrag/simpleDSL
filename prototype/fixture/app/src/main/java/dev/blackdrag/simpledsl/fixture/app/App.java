package dev.blackdrag.simpledsl.fixture.app;

import dev.blackdrag.simpledsl.fixture.core.Core;
import dev.blackdrag.simpledsl.fixture.generated.GeneratedValue;

public final class App {
    private App() {
    }

    public static String value() {
        return Core.value() + ":" + GeneratedValue.value();
    }
}
