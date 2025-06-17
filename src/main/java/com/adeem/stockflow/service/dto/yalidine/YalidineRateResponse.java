package com.adeem.stockflow.service.dto.yalidine;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Yalidine rate calculation response.
 */
public class YalidineRateResponse implements Serializable {

    private BigDecimal rate;
    private String currency;
    private String estimatedDelivery;

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }
}
