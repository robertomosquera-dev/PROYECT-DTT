package org.dtt.mscatalog.domain.model;


import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Getter
@SuperBuilder
public class SaleProduct extends BaseProductItem {

    private BigDecimal price;

    public void updatePrice(BigDecimal price) {
        validatePrice(price, "price");
        this.price = price;
    }
}
