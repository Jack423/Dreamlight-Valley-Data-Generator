package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Location extends AbstractEntity {

    private String icon;
    private String name;

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

}
