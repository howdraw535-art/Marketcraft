package dev.marketcraft.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class Optimiser {
    private final Map<String, List<Recipe>> byOutput = new HashMap<>();
    private final Map<String, Double> buyPrices;
    private final Map<String, Optional<Route>> cache = new HashMap<>();

    public Optimiser(List<Recipe> recipes, Map<String, Double> buyPrices) {
        this.buyPrices = buyPrices;
        for (Recipe r : recipes) {
            for (String out : r.outputs().keySet()) {
                byOutput.computeIfAbsent(out, k -> new ArrayList<>()).add(r);
            }
        }
    }

    public Optional<Route> cheapest(String item) {
        return solve(item, new HashSet<>());
    }

    private Optional<Route> solve(String item, Set<String> path) {
        Optional<Route> cached = cache.get(item);
        if (cached != null) return cached;
        if (path.contains(item)) return Optional.empty();
        path.add(item);
        boolean loopedAnywhere = false;

        Route best = null;
        Double price = buyPrices.get(item);
        if (price != null && price > 0) {
            best = new Route(item, true, price, null, List.of());
        }

        for (Recipe r : byOutput.getOrDefault(item, List.of())) {
            int out = r.outputCount(item);
            if (out <= 0) continue;
            double total = 0;
            boolean ok = true;
            List<Route.Step> steps = new ArrayList<>();
            for (Map.Entry<String, Integer> in : r.inputs().entrySet()) {
                if (path.contains(in.getKey())) {
                    loopedAnywhere = true;
                    ok = false;
                    break;
                }
                Optional<Route> sub = solve(in.getKey(), path);
                if (sub.isEmpty()) {
                    ok = false;
                    break;
                }
                total += sub.get().unitCost() * in.getValue();
                steps.add(new Route.Step(in.getKey(), in.getValue(), sub.get()));
            }
            if (!ok) continue;
            double unit = total / out;
            if (best == null || unit < best.unitCost()) {
                best = new Route(item, false, unit, r.id(), steps);
            }
        }

        path.remove(item);
        Optional<Route> result = Optional.ofNullable(best);
        if (!loopedAnywhere) cache.put(item, result);
        return result;
    }
}
