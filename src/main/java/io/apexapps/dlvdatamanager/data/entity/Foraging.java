package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Foraging extends AbstractEntity {

    private String acquisitionMethods;
    private String description;
    private String image;
    private String locations;
    private String name;
    private Integer sellPrice;
    private String type;

    public String getAcquisitionMethods() {
        return acquisitionMethods;
    }
    public void setAcquisitionMethods(String acquisitionMethods) {
        this.acquisitionMethods = acquisitionMethods;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getLocations() {
        return locations;
    }
    public void setLocations(String locations) {
        this.locations = locations;
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

}
