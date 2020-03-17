package com.example.multyplay;

import android.net.Uri;

public class Picture {

    private String name;
    private String url;

    public Picture(){
    }

    public Picture(String name, String url) {
        setName(name);
        setUri(url);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name.trim().equals("")) {
            this.name = "No name";
        } else {
            this.name = name;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUri(String url) {
        this.url = url;
    }
}
