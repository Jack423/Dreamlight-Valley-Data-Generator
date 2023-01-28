package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.models.CraftingRecipe;

import java.math.BigInteger;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefinedMaterialsModel {
    private BigInteger id;
    private String icon;
    private String name;
    private String description;
    private Integer buyPrice;
    private Integer sellPrice;
    private String soldAt;
    private List<CraftingRecipe> craftingRecipe;
}
