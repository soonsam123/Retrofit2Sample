package com.soon.karat.retrofitfs.models;

public class User {

    public Integer id;
    public String name;
    public String email;
    public int age;
    public String[] topics;

    public User(String name, String email, int age, String[] topics) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.topics = topics;
    }

    public Integer getId() {
        return id;
    }
}
