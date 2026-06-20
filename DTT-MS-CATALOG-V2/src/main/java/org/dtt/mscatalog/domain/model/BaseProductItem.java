package org.dtt.mscatalog.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.dtt.mscatalog.domain.exception.InvalidProductException;



// Base de modelo de tipos de productos de rental y sale
// Con logica repetida para los hijo lo heredan SaleProduct y ProductBundle
@Getter
@SuperBuilder
public class BaseProductItem extends BaseInventoryItem {

    private Product product;

    public void assignProduct(Product product) {
        if (product == null) {
            throw new InvalidProductException();
        }
        this.product = product;
    }
}