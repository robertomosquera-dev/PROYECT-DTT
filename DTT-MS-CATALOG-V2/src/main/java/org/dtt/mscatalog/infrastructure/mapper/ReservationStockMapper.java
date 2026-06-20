package org.dtt.mscatalog.infrastructure.mapper;

import org.dtt.mscatalog.application.dto.request.ReservationRequest;
import org.dtt.mscatalog.domain.model.ReservationStock;
import org.dtt.mscatalog.domain.model.ReservationItemStock;
import org.dtt.mscatalog.infrastructure.persistence.entity.ReservationStockEntity;
import org.dtt.mscatalog.infrastructure.persistence.entity.ReservationItemStockEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationStockMapper {

    @Mapping(target = "items", ignore = true)
    ReservationStock toDomain(ReservationRequest request); //Mapper para crear una reserva
    ReservationStockEntity toEntity(ReservationStock domain);
    List<ReservationItemStockEntity> toEntity(List<ReservationItemStock> domain);
    ReservationStock toDomain(ReservationStockEntity entity);

    List<ReservationItemStock> toDomain(List<ReservationItemStockEntity> entities);
    ReservationItemStock toDomain(ReservationItemStockEntity entity);
    ReservationItemStockEntity toEntity(ReservationItemStock domain);
}
