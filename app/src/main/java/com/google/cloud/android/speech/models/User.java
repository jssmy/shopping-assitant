package com.google.cloud.android.speech.models;

public class User {
String fullName;
String email;
String token;


    public User(String fullName, String email, String token) {
        this.fullName = fullName;
        this.email = email;
        this.token = token;
    }

    public User() {
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
}
