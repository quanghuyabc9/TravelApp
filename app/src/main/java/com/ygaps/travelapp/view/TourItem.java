package com.ygaps.travelapp.view;

public class TourItem {
    private Integer img_src;
    private String location;
    private String date;
    private String quantity;
    private String price;
    private boolean isHost;
    private int id;

    public TourItem(Integer img_src, String location, String date, String quantity, String price, int id, boolean isHost){
        this.img_src = img_src;
        this.location = location;
        this.date = date;
        this.quantity = quantity;
        this.price = price;
        this.isHost = isHost;
        this.id = id;
    }

    public void setImg_src(Integer img_src){
        this.img_src = img_src;
    }
    public void setLocation(String location){
        this.location = location;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setQuantity(String quantity){
        this.quantity = quantity;
    }
    public void setPrice(String price){
        this.price = price;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getImg_src() {
        return img_src;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public int getId() {
        return id;
    }

    public boolean isHost() {
        return isHost;
    }
}

