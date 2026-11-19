package dev.marketcraft.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PriceStats {
    private final int maxSamples;
    private final Map<String, OrderParser.Order> seen = new LinkedHashMap<>();
    private int observations = 0;
    private long lastUpdateMillis = 0;

    public PriceStats(int maxSamples) {
        this.maxSamples = Math.max(1, maxSamples);
    }

    public boolean add(OrderParser.Order order, long nowMillis) {
        observations++;
        lastUpdateMillis = nowMillis;
        return seen.putIfAbsent(order.key(), order) == null;
    }

    public List<OrderParser.Order> top() {
        List<OrderParser.Order> all = new ArrayList<>(seen.values());
        all.sort(Comparator.comparingDouble(OrderParser.Order::unitPrice).reversed());
        return all.size() <= maxSamples ? all : new ArrayList<>(all.subList(0, maxSamples));
    }

    public double average() {
        List<OrderParser.Order> t = top();
        if (t.isEmpty()) return 0.0;
        double sum = 0;
        for (OrderParser.Order o : t) sum += o.unitPrice();
        return sum / t.size();
    }

    public int distinctOrders() { return seen.size(); }
    public int observations() { return observations; }
    public long lastUpdateMillis() { return lastUpdateMillis; }
}
