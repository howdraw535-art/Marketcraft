package dev.marketcraft.core;

public final class Profit {
    private Profit() {}

    public static double revenue(double averageBid, int quantity, double feePercent) {
        return averageBid * quantity * (1.0 - feePercent / 100.0);
    }

    public static double profit(double averageBid, double unitCost, int quantity, double feePercent) {
        return revenue(averageBid, quantity, feePercent) - unitCost * quantity;
    }
}
