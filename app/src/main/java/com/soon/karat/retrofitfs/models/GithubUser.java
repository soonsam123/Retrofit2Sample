package com.soon.karat.retrofitfs.models;

public class GithubUser {

    private String login;
    private String name;
    private String avatar_url;
    private String company;
    private String bio;
    private String followers;
    private String following;

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getCompany() {
        return company;
    }

    public String getBio() {
        return bio;
    }

    public String getFollowers() {
        return followers;
    }

    public String getFollowing() {
        return following;
    }
}
