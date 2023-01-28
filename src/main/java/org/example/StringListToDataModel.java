package org.example;

import org.example.models.CraftingRecipe;

import java.util.List;

public class StringListToDataModel {
    public static List<CraftingRecipe> buildCraftingRecipe(String recipe) {
        List<String> recipeItem = List.of(recipe.split(","));

        return recipeItem.stream()
                .map(item -> {
                    CraftingRecipe.CraftingRecipeBuilder cr = CraftingRecipe.builder();
                    cr.name((item.split(",")[0]).split("\\(")[0].stripTrailing());

                    if (!item.contains("(")) {
                        cr.amount(1);
                    } else {
                        cr.amount(Integer.parseInt(item.replaceFirst(".*?(\\d+).*", "$1")));
                    }
                    return cr.build();
                })
                .toList();
    }
}
