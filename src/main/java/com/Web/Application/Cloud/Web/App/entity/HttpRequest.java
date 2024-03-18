package com.Web.Application.Cloud.Web.App.entity;

public class HttpRequest {
    private String requestMethod;
    private String requestUrl;
    private String requestSize;
    private String status;
    private String userAgent;
    private String serverIp;
    private String referrer;

    public HttpRequest() {
    }

    public HttpRequest(String requestMethod, String requestUrl, String requestSize, String status, String userAgent, String serverIp, String referrer) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.requestSize = requestSize;
        this.status = status;
        this.userAgent = userAgent;
        this.serverIp = serverIp;
        this.referrer = referrer;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestSize() {
        return requestSize;
    }

    public void setRequestSize(String requestSize) {
        this.requestSize = requestSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }
}
