package org.example.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CraftingItemEntity {
    private BigInteger id;
    private String icon;
    private String name;
    private String craftingRecipe;
    private CraftingItemType type;
}
