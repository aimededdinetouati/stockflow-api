package com.adeem.stockflow.service.dto.yalidine;

import java.io.Serializable;

/**
 * Generic Yalidine API response wrapper.
 */
public class YalidineApiResponse implements Serializable {

    private boolean success;
    private String message;
    private Object data;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
