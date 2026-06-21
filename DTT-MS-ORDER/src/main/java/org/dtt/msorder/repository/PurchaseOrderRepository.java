package org.dtt.msorder.repository;

import java.util.Optional;
import java.util.UUID;

import org.dtt.msorder.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder,UUID> {
    Optional<PurchaseOrder> findByIdAndUserId(UUID id, UUID userId);
}
