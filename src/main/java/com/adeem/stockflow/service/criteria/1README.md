# Advanced Query Specifications for StockFlow

This directory contains specification classes for building dynamic, complex queries using Spring Data JPA's Specification interface. These specifications provide a flexible way to filter and search for entities with multiple criteria.

## Overview

The specification classes follow a pattern where each static method creates a specification for a specific filter condition. These conditions can then be combined using logical operators (AND, OR) to build complex queries.

## Base Classes

- **BaseSpecification**: Contains common filtering operations like equals, contains, greaterThan, lessThan, etc.
- **SpecificationBuilder**: Helper class for combining multiple specifications with AND or OR logic.

## Entity-Specific Specifications

- **ProductSpecification**: Filters for Product entities
- **CustomerSpecification**: Filters for Customer entities
- **SaleOrderSpecification**: Filters for SaleOrder entities
- **InventorySpecification**: Filters for Inventory entities
- **PaymentSpecification**: Filters for Payment entities
- **ShipmentSpecification**: Filters for Shipment entities
- **SupplierSpecification**: Filters for Supplier entities
- **ClientAccountSpecification**: Filters for ClientAccount entities
- **SubscriptionSpecification**: Filters for Subscription entities
- **ReturnOrderSpecification**: Filters for ReturnOrder entities
- **PurchaseOrderSpecification**: Filters for PurchaseOrder entities
- **InventoryTransactionSpecification**: Filters for InventoryTransaction entities

## Usage Examples

### Basic Example

```java
// Find all products with name containing "laptop" and price between 500 and 1000
Specification<Product> spec = Specification.where(ProductSpecification.withName("laptop")).and(
  ProductSpecification.withSellingPriceBetween(new BigDecimal("500"), new BigDecimal("1000"))
);

List<Product> products = productRepository.findAll(spec);

```

### Using SpecificationBuilder

```java
// Search for products with optional filters
Specification<Product> spec = new SpecificationBuilder<Product>()
  .withString(name, ProductSpecification::withName)
  .with(minPrice, ProductSpecification::withSellingPriceGreaterThanOrEqual)
  .with(maxPrice, ProductSpecification::withSellingPriceLessThanOrEqual)
  .with(category, ProductSpecification::withCategory)
  .with(visibleToCustomers, ProductSpecification::withVisibleToCustomers)
  .build();

Page<Product> products = productRepository.findAll(spec, pageable);

```

### Complex Query Example

```java
// Find overdue sale orders for a specific customer and client account
Specification<SaleOrder> spec = new SpecificationBuilder<SaleOrder>()
  .with(customerId, SaleOrderSpecification::withCustomerId)
  .with(clientAccountId, SaleOrderSpecification::withClientAccountId)
  .with(SaleOrderSpecification.isOverdue())
  .build();

List<SaleOrder> overdueSaleOrders = saleOrderRepository.findAll(spec);

```

## API Integration

The `AdvancedSearchResource` class demonstrates how to use these specifications in REST endpoints to create flexible search APIs. For example:

```
GET /api/advanced/products/search?name=laptop&minPrice=500&maxPrice=1000&category=electronics
```

This approach allows clients to filter data with dynamic criteria without needing separate endpoints for each use case.

## Benefits

1. **Type Safety**: Specifications provide type-safe queries compared to string-based approaches.
2. **Reusability**: Specification methods can be reused across different parts of the application.
3. **Composability**: Individual specifications can be combined to create complex queries.
4. **Maintainability**: Queries are organized logically by entity and criteria.
5. **Flexibility**: Supports optional parameters and dynamic query building.

## Implementation Notes

- All specifications use null-safe checks to handle optional parameters.
- Join operations are performed with LEFT JOIN to avoid excluding records when related entities don't match.
- Complex specifications include methods for common business use cases like finding low stock, overdue orders, etc.
