package com.adeem.stockflow.service.mapper;

import com.adeem.stockflow.domain.Attachment;
import com.adeem.stockflow.domain.Inventory;
import com.adeem.stockflow.domain.Product;
import com.adeem.stockflow.service.dto.AttachmentDTO;
import com.adeem.stockflow.service.dto.InventoryDTO;
import com.adeem.stockflow.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Inventory} and its DTO {@link InventoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper extends EntityMapper<InventoryDTO, Inventory> {
    @Mapping(target = "productId", source = "product.id")
    InventoryDTO toDto(Inventory s);

    @Mapping(target = "product", source = "productId")
    Inventory toEntity(InventoryDTO inventoryDTO);

    default Product fromProductId(Long id) {
        if (id == null) {
            return null;
        }
        Product product = new Product();
        product.setId(id);
        return product;
    }

    default Inventory fromId(Long id) {
        if (id == null) {
            return null;
        }
        Inventory inventory = new Inventory();
        inventory.setId(id);
        return inventory;
    }
}
