package com.example.multyplay;

public class SearchFilters {


    private int maxDistance;
    private int minAge;
    private int maxAge;

    public SearchFilters() {
    }

    public SearchFilters(int maxDistance, int minAge, int maxAge) {
        setMaxDistance(maxDistance);
        setMinAge(minAge);
        setMaxAge(maxAge);
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

}
