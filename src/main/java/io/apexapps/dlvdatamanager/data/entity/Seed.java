package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Seed extends AbstractEntity {

    private String growTime;
    private String icon;
    private String ingredientType;
    private String name;
    private String nativeBiome;
    private Integer seedPrice;
    private Integer sellPrice;
    private Integer waterings;
    private Integer yield;

    public String getGrowTime() {
        return growTime;
    }
    public void setGrowTime(String growTime) {
        this.growTime = growTime;
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
    public String getNativeBiome() {
        return nativeBiome;
    }
    public void setNativeBiome(String nativeBiome) {
        this.nativeBiome = nativeBiome;
    }
    public Integer getSeedPrice() {
        return seedPrice;
    }
    public void setSeedPrice(Integer seedPrice) {
        this.seedPrice = seedPrice;
    }
    public Integer getSellPrice() {
        return sellPrice;
    }
    public void setSellPrice(Integer sellPrice) {
        this.sellPrice = sellPrice;
    }
    public Integer getWaterings() {
        return waterings;
    }
    public void setWaterings(Integer waterings) {
        this.waterings = waterings;
    }
    public Integer getYield() {
        return yield;
    }
    public void setYield(Integer yield) {
        this.yield = yield;
    }

}
