package com.sharepad.dto;

public class LoginResponse {
    private String url;
    private String email;
    private String name;
    private boolean loggedIn;

    public LoginResponse(String url, String email, String name, boolean loggedIn) {
        this.url = url;
        this.email = email;
        this.name = name;
        this.loggedIn = loggedIn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
