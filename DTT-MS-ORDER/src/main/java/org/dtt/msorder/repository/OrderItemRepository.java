package org.dtt.msorder.repository;

import java.util.UUID;

import org.dtt.msorder.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,UUID>{

}