package com.example.multyplay;


import android.graphics.drawable.Drawable;

public class Language {

    private String langName;
    private int flag;

    public Language(){
    }

    public Language(String langName, int flag){
        setLangName(langName);
        setFlag(flag);
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getLangName() {
        return langName;
    }

    public void setLangName(String langName) {
        this.langName = langName;
    }

}
