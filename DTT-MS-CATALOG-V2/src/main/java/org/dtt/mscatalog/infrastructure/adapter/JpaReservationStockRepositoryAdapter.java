package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.ReservationStockRepositoryPort;
import org.dtt.mscatalog.domain.model.ReservationStock;
import org.dtt.mscatalog.infrastructure.mapper.ReservationStockMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.ReservationStockEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataReservationStockRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReservationStockRepositoryAdapter implements ReservationStockRepositoryPort {

    private final SpringDataReservationStockRepository springDataReservationStockRepository;
    private final ReservationStockMapper reservationStockMapper;

    @Override
    public ReservationStock save(ReservationStock domain) {
        ReservationStockEntity saved = domain.getId() != null
                ? update(domain)
                : create(domain);
        return reservationStockMapper.toDomain(saved);
    }

    private ReservationStockEntity create(ReservationStock domain) {
        ReservationStockEntity entity = reservationStockMapper.toEntity(domain);
        entity.getItems().forEach(item -> item.setReservation(entity));
        return springDataReservationStockRepository.save(entity);
    }

    private ReservationStockEntity update(ReservationStock domain) {
        ReservationStockEntity entity = springDataReservationStockRepository
                .findById(domain.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "ReservationStock not found: " + domain.getId()));
        entity.setEstado(domain.getEstado());
        return springDataReservationStockRepository.save(entity);
    }

    @Override
    public Optional<ReservationStock> findById(UUID id) {
        return springDataReservationStockRepository.findById(id)
                .map(reservationStockMapper::toDomain);
    }
}
