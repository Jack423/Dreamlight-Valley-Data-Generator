package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Ingredient extends AbstractEntity {

    private Integer buyPrice;
    private String description;
    private Integer energy;
    private String growTime;
    private boolean hidden;
    private String icon;
    private String ingredientType;
    private String name;
    private Integer sellPrice;
    private Integer water;
    private Integer yield;
    private String location;

    public Integer getBuyPrice() {
        return buyPrice;
    }
    public void setBuyPrice(Integer buyPrice) {
        this.buyPrice = buyPrice;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getEnergy() {
        return energy;
    }
    public void setEnergy(Integer energy) {
        this.energy = energy;
    }
    public String getGrowTime() {
        return growTime;
    }
    public void setGrowTime(String growTime) {
        this.growTime = growTime;
    }
    public boolean isHidden() {
        return hidden;
    }
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getIngredientType() {
        return ingredientType;
    }
    public void setIngredientType(String ingredientType) {
        this.ingredientType = ingredientType;
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
    public Integer getWater() {
        return water;
    }
    public void setWater(Integer water) {
        this.water = water;
    }
    public Integer getYield() {
        return yield;
    }
    public void setYield(Integer yield) {
        this.yield = yield;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

}
