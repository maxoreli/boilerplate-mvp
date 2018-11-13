package com.tizisolutions.boilerplate_code.data.model;

import java.io.Serializable;

public class MSession implements Serializable{

    String accessToken;
    String refreshToken;
    String fbAuthToken;

    boolean isExpired;

    public MSession(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public MSession(String accessToken, String refreshToken, String fbAuthToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.fbAuthToken = fbAuthToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public void setFbAuthToken(String fbAuthToken) {
        this.fbAuthToken = fbAuthToken;
    }

    public String getFbAuthToken() {
        return fbAuthToken;
    }
}
