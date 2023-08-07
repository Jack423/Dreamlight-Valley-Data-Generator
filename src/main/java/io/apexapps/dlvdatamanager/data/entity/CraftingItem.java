package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class CraftingItem extends AbstractEntity {

    private String craftingRecipe;
    private String icon;
    private String name;
    private String type;

    public String getCraftingRecipe() {
        return craftingRecipe;
    }
    public void setCraftingRecipe(String craftingRecipe) {
        this.craftingRecipe = craftingRecipe;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

}
