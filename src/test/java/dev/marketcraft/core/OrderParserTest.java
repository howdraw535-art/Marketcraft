package dev.marketcraft.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OrderParserTest {
    @Test
    void parsesRealTooltip() {
        Optional<OrderParser.Order> o = OrderParser.parse(
            List.of("Maces", "$ 8M each", "0/2 Delivered", "Click to deliver items"));
        assertTrue(o.isPresent());
        assertEquals("Maces", o.get().itemName());
        assertEquals(8_000_000.0, o.get().unitPrice(), 0.001);
        assertEquals(2, o.get().remaining());
    }

    @Test
    void parsesCommasAndDecimals() {
        Optional<OrderParser.Order> o = OrderParser.parse(
            List.of("Gold", "$1,250.5K each", "10/64 Delivered"));
        assertTrue(o.isPresent());
        assertEquals(1_250_500.0, o.get().unitPrice(), 0.001);
        assertEquals(54, o.get().remaining());
    }

    @Test
    void rejectsTotalPrices() {
        assertTrue(OrderParser.parse(List.of("Gold", "$5 total", "0/2 Delivered")).isEmpty());
    }

    @Test
    void rejectsFullyDelivered() {
        assertTrue(OrderParser.parse(List.of("Maces", "$8M each", "2/2 Delivered")).isEmpty());
    }

    @Test
    void rejectsGarbageSafely() {
        assertTrue(OrderParser.parse(List.of()).isEmpty());
        assertTrue(OrderParser.parse(null).isEmpty());
        assertTrue(OrderParser.parse(List.of("Gold", "garbage", "0/2 Delivered")).isEmpty());
        assertTrue(OrderParser.parse(List.of("Gold", "$8M each", "5/2 Delivered")).isEmpty());
    }
}
