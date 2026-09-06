package dev.blackdrag.simpledsl.fixture.web;

import dev.blackdrag.simpledsl.fixture.app.App;

public final class Web {
    private Web() {
    }

    public static String value() {
        return App.value();
    }
}
