package com.ratila.universitybase1;

import org.jetbrains.annotations.UnmodifiableView;

public class University {
    private int id;
    private String name;
    private int cityId;
    private String description;
    private String contacts;
    private String website;
    private String imageUrl; // Добавляем поле для URL картинки

    public University() {
    }
    // Конструктор с 6 параметрами (без imageUrl)
    public University(int id, String name, int cityId, String description, String contacts, String website) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.description = description;
        this.contacts = contacts;
        this.website = website;
    }

    // Новый конструктор с 7 параметрами (с imageUrl)
    public University(int id, String name, int cityId, String description, String contacts, String website, String imageUrl) {
        this.id = id;
        this.name = name;
        this.cityId = cityId;
        this.description = description;
        this.contacts = contacts;
        this.website = website;
        this.imageUrl = imageUrl; // Инициализируем imageUrl
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}