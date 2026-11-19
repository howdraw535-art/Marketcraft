package dev.marketcraft.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PriceStatsTest {
    private OrderParser.Order order(double price, int wanted) {
        return new OrderParser.Order("Maces", price, wanted, 0, wanted);
    }

    @Test
    void averagesTopTen() {
        PriceStats s = new PriceStats(10);
        for (int i = 1; i <= 12; i++) s.add(order(i * 100.0, i), 1L);
        assertEquals(10, s.top().size());
        assertEquals(750.0, s.average(), 0.001);
    }

    @Test
    void dedupesRescans() {
        PriceStats s = new PriceStats(10);
        assertTrue(s.add(order(500, 2), 1L));
        assertFalse(s.add(order(500, 2), 2L));
        assertEquals(1, s.distinctOrders());
        assertEquals(2, s.observations());
    }

    @Test
    void emptyAverageIsZero() {
        assertEquals(0.0, new PriceStats(10).average(), 0.0);
    }
}
