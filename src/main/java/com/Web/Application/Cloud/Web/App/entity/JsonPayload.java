package com.Web.Application.Cloud.Web.App.entity;

public class JsonPayload {
    private String user;

    public JsonPayload() {
    }

    public JsonPayload(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
