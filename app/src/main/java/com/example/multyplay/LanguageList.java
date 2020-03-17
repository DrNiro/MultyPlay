package com.example.multyplay;

import java.util.ArrayList;

public class LanguageList {

    private ArrayList<Language> languages;

    public LanguageList() {
        setLanguages(new ArrayList<Language>());
    }

    public LanguageList(ArrayList<Language> languages) {
        setLanguages(languages);
    }

    public int Size() {
        return this.getLanguages().size();
    }

    public void addLanguage(Language language) {
        ArrayList<Language> newList = getLanguages();
        newList.add(language);
        setLanguages(newList);
    }

    public ArrayList<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<Language> languages) {
        this.languages = languages;
    }
}
