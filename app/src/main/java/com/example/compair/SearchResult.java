package com.example.compair;

public class SearchResult {
    private String name;
    private String id;
    private String category; // new field
    private double price;         // discounted price
    private int actualPrice;   // actual price
    private double rating;
    private int reviews;
    private String url;
    private String imageUrl;

    public SearchResult() {
    }

    public SearchResult(String name, String category, int price, int actualPrice, double rating, int reviews, String url, String imageUrl) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.actualPrice = actualPrice;
        this.rating = rating;
        this.reviews = reviews;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    public SearchResult(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.actualPrice = price;
        this.rating = 0.0;
        this.reviews = 0;
        this.url = "N/A";
        this.imageUrl = imageUrl;
    }
    public SearchResult(String id,String name,String category, double price, String imageUrl) {
        this.name = name;
        this.id=id;
        this.category=category;
        this.price=price;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getName() { return name != null ? name : "Unknown"; }
    public String getCategory() { return category != null ? category : "Uncategorized"; }
    public double getPrice() { return price; }
    public int getActualPrice() { return actualPrice; }
    public double getRating() { return rating; }
    public int getReviews() { return reviews; }
    public String getUrl() { return url != null ? url : "N/A"; }
    public String getImageUrl() { return imageUrl != null ? imageUrl : ""; }
    public String getId() {
        return id;
    }
    // Setters
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(int price) { this.price = price; }
    public void setActualPrice(int actualPrice) { this.actualPrice = actualPrice; }
    public void setRating(double rating) { this.rating = rating; }
    public void setReviews(int reviews) { this.reviews = reviews; }
    public void setUrl(String url) { this.url = url; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
