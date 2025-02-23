package com.example.compair;

public class Category {
    private String name;
    private int imageResId;  // Stores the ID of an image resource

    public Category(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
