package com.soon.karat.retrofitfs.models;

public class GitHubRepo {

    public String name;
    public String language;
    public Owner owner;

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public Owner getOwner() {
        return owner;
    }
}
