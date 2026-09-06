package dev.blackdrag.simpledsl.fixture.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {
    @Test
    void generatedSourceIsAvailableToApplication() {
        assertEquals("core:generated", App.value());
    }
}
