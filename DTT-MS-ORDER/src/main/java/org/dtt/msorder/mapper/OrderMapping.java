package org.dtt.msorder.mapper;

import org.dtt.msorder.dto.Response.OrderDetailsResponse;
import org.dtt.msorder.model.OrderItem;
import org.dtt.msorder.model.PurchaseOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapping {

    @Mapping(target = "totalItems", source = "order.items", qualifiedByName = "countItems")
    @Mapping(target = "totalPerItem", source = "totalPerItem")
    OrderDetailsResponse toDetailsResponse(PurchaseOrder order,
                                           Integer totalPerItem);

    @Named("countItems")
    default Integer countItems(List<OrderItem> items) {
        return items != null ? items.size() : 0;
    }
}
