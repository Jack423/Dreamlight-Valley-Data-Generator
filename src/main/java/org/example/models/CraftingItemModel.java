package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entities.CraftingItemEntity;
import org.example.entities.CraftingItemType;

import java.math.BigInteger;
import java.util.List;

import static org.example.StringListToDataModel.buildCraftingRecipe;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CraftingItemModel {
    private BigInteger id;
    private String icon;
    private String name;
    private List<CraftingRecipe> craftingRecipe;
    private CraftingItemType type;

    public static CraftingItemModel fromEntity(CraftingItemEntity entity) {
        return CraftingItemModel.builder()
                .id(entity.getId())
                .icon(entity.getIcon())
                .name(entity.getName())
                .craftingRecipe(buildCraftingRecipe(entity.getCraftingRecipe()))
                .type(entity.getType())
                .build();
    }
}
