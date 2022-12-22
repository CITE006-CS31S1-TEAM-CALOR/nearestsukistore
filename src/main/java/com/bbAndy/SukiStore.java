package com.bbAndy;

public class SukiStore {
    private double latitude;
    private double longitude;
    private String storeName;
    private double distanceAwayMeters;
  
    public SukiStore() {
    }
    
    public SukiStore(String storeName, double latitude, double longitude, double distanceAwayMeters) {
       super();
       this.storeName = storeName;
       this.latitude = latitude;
       this.longitude = longitude;
       this.distanceAwayMeters = distanceAwayMeters;
    }
}
