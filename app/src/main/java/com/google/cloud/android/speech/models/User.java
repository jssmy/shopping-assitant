package com.google.cloud.android.speech.models;

public class User {
int id;
String fullName;
String email;
String token;
String password;


    public User(String fullName, String email, String token) {
        this.fullName = fullName;
        this.email = email;
        this.token = token;
    }

    public User(int id, String fullName, String email, String token) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.token = token;
    }

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
