package com.example.compair;


public class CartItem {
    public String productName;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    int quantity;
    int id;

    public CartItem(String name, String description, double price, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public CartItem(String name, int newQuantity) {
        this.name = name;
        this.quantity=newQuantity;
    }

    public CartItem(String productId, String name, int quantity, double price) {
        
        

    }


    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}
