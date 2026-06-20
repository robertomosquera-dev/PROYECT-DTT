package org.dtt.mscatalog.application.dto;


import java.math.BigDecimal;

public interface ISellableInventory {
    BigDecimal price();
    Integer stock();
}