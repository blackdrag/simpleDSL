package dev.blackdrag.simpledsl.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BuildModelTest {
    @Test
    void describesTheInitialBackendExperiment() {
        BuildModel model = InitialExperiment.create();

        assertEquals(4, model.modules().size());
        assertEquals(2, model.module("app").dependencies().size());
        assertTrue(model.module("app").dependencies().stream()
                .anyMatch(d -> d.scope() == DependencyScope.BUILD && d.target().equals("generator")));
        assertEquals(1, model.generations().size());
        Generation generation = model.generations().getFirst();
        assertEquals("generator", generation.generatorModule());
        assertEquals("app", generation.targetModule());
    }
}
