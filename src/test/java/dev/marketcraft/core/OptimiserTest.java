package dev.marketcraft.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OptimiserTest {
    private List<Recipe> recipes() {
        return List.of(
            new Recipe("ingot_from_nuggets", Map.of("gold_ingot", 1), Map.of("gold_nugget", 9)),
            new Recipe("ingot_from_block", Map.of("gold_ingot", 9), Map.of("gold_block", 1)),
            new Recipe("nugget_from_ingot", Map.of("gold_nugget", 9), Map.of("gold_ingot", 1)),
            new Recipe("golden_apple", Map.of("golden_apple", 1),
                Map.of("gold_ingot", 8, "apple", 1)));
    }

    @Test
    void picksCheapestRouteAndHandlesLoops() {
        Map<String, Double> buy = Map.of("gold_nugget", 10.0, "apple", 5.0, "gold_block", 800.0);
        Optimiser o = new Optimiser(recipes(), buy);
        Optional<Route> ingot = o.cheapest("gold_ingot");
        assertTrue(ingot.isPresent());
        assertEquals(800.0 / 9.0, ingot.get().unitCost(), 0.001);
    }

    @Test
    void chainsThroughLayers() {
        Map<String, Double> buy = Map.of("gold_nugget", 10.0, "apple", 5.0, "gold_block", 800.0);
        Optimiser o = new Optimiser(recipes(), buy);
        Optional<Route> apple = o.cheapest("golden_apple");
        assertTrue(apple.isPresent());
        assertEquals(8 * (800.0 / 9.0) + 5.0, apple.get().unitCost(), 0.001);
    }

    @Test
    void returnsEmptyWhenNoRoute() {
        Optimiser o = new Optimiser(recipes(), Map.of());
        assertTrue(o.cheapest("golden_apple").isEmpty());
    }
}
