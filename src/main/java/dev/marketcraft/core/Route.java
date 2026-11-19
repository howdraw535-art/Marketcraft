package dev.marketcraft.core;

import java.util.List;

public record Route(String item, boolean bought, double unitCost, String recipeId, List<Step> steps) {
    public record Step(String ingredient, int amountPerCraft, Route route) {}
}
