package org.dtt.msorder.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import org.dtt.msorder.dto.Request.ItemRequest;
import org.dtt.msorder.dto.Response.ItemCatalogResponse;
import org.dtt.msorder.model.OrderItem;
import org.dtt.msorder.model.PurchaseOrder;
import org.dtt.msorder.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {
    
    private final OrderItemRepository itemRepository;


    private static OrderItem ItemOptime(PurchaseOrder order,ItemCatalogResponse itemRequest) {
        BigDecimal subTotal = itemRequest
                .unitPrice()
                .multiply(BigDecimal.valueOf(itemRequest.quantity()));
        return OrderItem.builder()
                .productId(itemRequest.productId())
                .quantity(itemRequest.quantity())
                .unitPrice(itemRequest.unitPrice())
                .subtotal(subTotal)
                .order(order)
                .build();
    }

    private static List<OrderItem> ItemMapListOptime(PurchaseOrder order,List<ItemCatalogResponse>itemRequests){
        return itemRequests
                .stream()
                .map(item -> ItemOptime(order,item))
                .toList();
    }

    public List<OrderItem> saveItems (PurchaseOrder order,List<ItemCatalogResponse>itemRequests){
        List<OrderItem> items = ItemMapListOptime(order,itemRequests);
        return itemRepository.saveAll(items);
    }



    public void deleteItems(List<OrderItem> item){
        itemRepository.deleteAll(item);
    }

}
