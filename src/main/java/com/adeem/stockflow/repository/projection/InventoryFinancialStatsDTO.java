package com.adeem.stockflow.repository.projection;

import java.math.BigDecimal;

public record InventoryFinancialStatsDTO(
    BigDecimal totalUnits,
    BigDecimal totalAvailable,
    BigDecimal totalReserved,
    BigDecimal totalValue
) {
    public InventoryFinancialStatsDTO(Number totalUnits, Number totalAvailable, Number totalReserved, Number totalValue) {
        this(
            totalUnits != null ? new BigDecimal(totalUnits.toString()) : BigDecimal.ZERO,
            totalAvailable != null ? new BigDecimal(totalAvailable.toString()) : BigDecimal.ZERO,
            totalReserved != null ? new BigDecimal(totalReserved.toString()) : BigDecimal.ZERO,
            totalValue != null ? new BigDecimal(totalValue.toString()) : BigDecimal.ZERO
        );
    }
}
