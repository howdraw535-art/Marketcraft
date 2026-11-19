package dev.marketcraft.core;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OrderParser {
    private static final Pattern PRICE = Pattern.compile(
        "^\\$?\\s*([0-9][0-9,]*(?:\\.[0-9]+)?)\\s*([KMBTkmbt]?)\\s*each$",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern DELIVERED = Pattern.compile(
        "^(\\d+)\\s*/\\s*(\\d+)\\s*Delivered$", Pattern.CASE_INSENSITIVE);

    private OrderParser() {}

    public record Order(String itemName, double unitPrice, int remaining, int delivered, int wanted) {
        public String key() {
            return itemName + "|" + unitPrice + "|" + wanted;
        }
    }

    public static double suffixMultiplier(String s) {
        if (s == null || s.isEmpty()) return 1.0;
        switch (Character.toUpperCase(s.charAt(0))) {
            case 'K': return 1e3;
            case 'M': return 1e6;
            case 'B': return 1e9;
            case 'T': return 1e12;
            default: return 1.0;
        }
    }

    public static Optional<Order> parse(List<String> lines) {
        if (lines == null || lines.isEmpty()) return Optional.empty();
        String name = lines.get(0) == null ? "" : lines.get(0).trim();
        if (name.isEmpty()) return Optional.empty();
        Double price = null;
        Integer got = null;
        Integer want = null;
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line == null) continue;
            line = line.trim();
            Matcher pm = PRICE.matcher(line);
            if (price == null && pm.matches()) {
                try {
                    double base = Double.parseDouble(pm.group(1).replace(",", ""));
                    price = base * suffixMultiplier(pm.group(2));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
                continue;
            }
            Matcher dm = DELIVERED.matcher(line);
            if (dm.matches()) {
                try {
                    got = Integer.parseInt(dm.group(1));
                    want = Integer.parseInt(dm.group(2));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            }
        }
        if (price == null || want == null || got == null) return Optional.empty();
        if (want <= 0 || got < 0 || got > want) return Optional.empty();
        if (!(price > 0) || Double.isInfinite(price)) return Optional.empty();
        int remaining = want - got;
        if (remaining <= 0) return Optional.empty();
        return Optional.of(new Order(name, price, remaining, got, want));
    }
}
