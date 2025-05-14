package com.example.EVCharge.dto;

public class LoginResponse {
    private boolean success;
    private String redirectUrl;
    private String token;

    public LoginResponse(boolean success, String redirectUrl, String token) {
        this.success = success;
        this.redirectUrl = redirectUrl;
        this.token = token;
    }

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

