package org.example;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.models.CharacterModel;
import org.example.models.CraftingModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .addModules(new JavaTimeModule())
                .build();
        CraftingDataBuilder craftingDataBuilder = new CraftingDataBuilder(mapper);

        CraftingModel crafting = CraftingModel.builder()
                .refinedMaterials(craftingDataBuilder.convertRefinedMaterials())
                .everythingElse(craftingDataBuilder.convertEverythingElse())
                .build();

        mapper.writeValue(new File("src/main/resources/crafting-output.json"), crafting);

        CharacterDataBuilder characterDataBuilder = new CharacterDataBuilder(mapper);

        List<CharacterModel> characterModelList = characterDataBuilder.convertCharacters();

        mapper.writeValue(new File("src/main/resources/characters-output.json"), characterModelList);
    }
}