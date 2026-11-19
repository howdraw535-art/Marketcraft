package dev.marketcraft.core;

import java.util.Map;

public record Recipe(String id, Map<String, Integer> outputs, Map<String, Integer> inputs) {
    public int outputCount(String item) {
        return outputs.getOrDefault(item, 0);
    }
}
