package io.apexapps.dlvdatamanager.data.entity;

import jakarta.persistence.Entity;

@Entity
public class Character extends AbstractEntity {

    private String characterRole;
    private String icon;
    private String information;
    private Integer level;
    private String name;
    private String quests;
    private String schedule;
    private String scheduleString;

    public String getCharacterRole() {
        return characterRole;
    }
    public void setCharacterRole(String characterRole) {
        this.characterRole = characterRole;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getInformation() {
        return information;
    }
    public void setInformation(String information) {
        this.information = information;
    }
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getQuests() {
        return quests;
    }
    public void setQuests(String quests) {
        this.quests = quests;
    }
    public String getSchedule() {
        return schedule;
    }
    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
    public String getScheduleString() {
        return scheduleString;
    }
    public void setScheduleString(String scheduleString) {
        this.scheduleString = scheduleString;
    }

}
