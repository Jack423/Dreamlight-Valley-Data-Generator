package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.example.entities.CraftingEntity;
import org.example.models.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.example.StringListToDataModel.buildCraftingRecipe;

@AllArgsConstructor
public class CraftingDataBuilder {
    private static final String CRAFTING_PATH = "src/main/resources/crafting.json";
    private final ObjectMapper mapper;

    public List<RefinedMaterialsModel> convertRefinedMaterials() throws IOException {
        CraftingEntity craftingEntity = mapper.readValue(
                new File(CRAFTING_PATH),
                CraftingEntity.class
        );

        return craftingEntity.getRefinedMaterials().stream()
                .map(material -> RefinedMaterialsModel.builder()
                        .id(material.getId())
                        .icon(material.getIcon())
                        .name(material.getName())
                        .description(material.getDescription())
                        .buyPrice(material.getBuyPrice())
                        .sellPrice(material.getSellPrice())
                        .soldAt(material.getSoldAt())
                        .craftingRecipe(buildCraftingRecipe(material.getCraftingRecipe()))
                        .build())
                .toList();
    }

    public List<CraftingItemModel> convertEverythingElse() throws IOException {
        CraftingEntity craftingEntity = mapper.readValue(
                new File(CRAFTING_PATH),
                CraftingEntity.class
        );

        return craftingEntity.getEverythingElse().stream()
                .map(CraftingItemModel::fromEntity)
                .toList();
    }
}
