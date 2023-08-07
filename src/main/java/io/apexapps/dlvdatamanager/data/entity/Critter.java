package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Critter extends AbstractEntity {

    private String name;
    private String icon;
    private String howToFeed;
    private String type;
    private String favoriteFood;
    private String location;
    private String schedule;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getHowToFeed() {
        return howToFeed;
    }
    public void setHowToFeed(String howToFeed) {
        this.howToFeed = howToFeed;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFavoriteFood() {
        return favoriteFood;
    }
    public void setFavoriteFood(String favoriteFood) {
        this.favoriteFood = favoriteFood;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getSchedule() {
        return schedule;
    }
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

}
