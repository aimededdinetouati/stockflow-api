package com.adeem.stockflow.service.dto.yalidine;

import java.io.Serializable;

/**
 * Yalidine tracking API response wrapper.
 */
public class YalidineTrackingApiResponse implements Serializable {

    private boolean success;
    private String message;
    private Object data;

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
}
