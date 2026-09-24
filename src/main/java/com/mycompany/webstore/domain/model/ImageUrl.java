package com.mycompany.webstore.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImageUrl {

    private String url;
    private String alt;
    private int position;

    @JsonProperty("is_primary")
    private boolean isPrimary;

    public ImageUrl() {}

    public ImageUrl(String url, String alt, int position, boolean isPrimary) {
        this.url = url;
        this.alt = alt;
        this.position = position;
        this.isPrimary = isPrimary;
    }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAlt() { return alt; }
    public void setAlt(String alt) { this.alt = alt; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    @JsonProperty("is_primary")
    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean isPrimary) { this.isPrimary = isPrimary; }
}

