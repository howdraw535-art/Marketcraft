package dev.marketcraft.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProfitTest {
    @Test
    void appliesFee() {
        assertEquals(98.0, Profit.revenue(100.0, 1, 2.0), 0.001);
    }

    @Test
    void profitAcrossBatch() {
        assertEquals(48.0, Profit.profit(100.0, 50.0, 1, 2.0), 0.001);
    }
}
