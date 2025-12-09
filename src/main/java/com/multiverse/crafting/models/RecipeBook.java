package com.multiverse.crafting.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple holder for player's known recipes.
 */
public class RecipeBook {

    private final Set<String> knownRecipes = new HashSet<>();

    public boolean knows(String recipeId) {
        return knownRecipes.contains(recipeId);
    }

    public void learn(String recipeId) {
        knownRecipes.add(recipeId);
    }

    public Set<String> getKnownRecipes() {
        return knownRecipes;
    }
}