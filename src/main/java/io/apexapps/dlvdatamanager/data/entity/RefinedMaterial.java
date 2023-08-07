package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class RefinedMaterial extends AbstractEntity {

    private Integer buyPrice;
    private String craftingRecipe;
    private String description;
    private String icon;
    private String name;
    private Integer sellPrice;
    private String soldAt;

    public Integer getBuyPrice() {
        return buyPrice;
    }
    public void setBuyPrice(Integer buyPrice) {
        this.buyPrice = buyPrice;
    }
    public String getCraftingRecipe() {
        return craftingRecipe;
    }
    public void setCraftingRecipe(String craftingRecipe) {
        this.craftingRecipe = craftingRecipe;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public Integer getSellPrice() {
        return sellPrice;
    }
    public void setSellPrice(Integer sellPrice) {
        this.sellPrice = sellPrice;
    }
    public String getSoldAt() {
        return soldAt;
    }
    public void setSoldAt(String soldAt) {
        this.soldAt = soldAt;
    }

}
