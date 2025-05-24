package com.adeem.stockflow.repository.projection;

public record InventoryStockLevelStatsDTO(
    Long totalProducts,
    Long outOfStockCount,
    Long lowStockCount,
    Long healthyStockCount,
    Long overstockCount
) {}
