package com.imeja.carpooling.auth.phone;

import java.io.Serializable;

public class BaseBean implements Serializable {

    protected boolean webError;
    protected String status;
    protected String error;
    protected String errorMsg;
    protected String webMessage;

    public String getWebMessage() {
        return webMessage;
    }

    public void setWebMessage(String webMessage) {
        this.webMessage = webMessage;
    }

    public boolean isWebError() {
        return webError;
    }

    public void setWebError(boolean webError) {
        this.webError = webError;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
