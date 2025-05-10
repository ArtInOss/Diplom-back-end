package com.example.EVCharge.controllers;

public class LoginResponse {
    private boolean success;       // Успех авторизации
    private String redirectUrl;    // URL для редиректа
    private String token;          // JWT токен

    // Конструктор
    public LoginResponse(boolean success, String redirectUrl, String token) {
        this.success = success;
        this.redirectUrl = redirectUrl;
        this.token = token;
    }

    // Геттеры и сеттеры
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

