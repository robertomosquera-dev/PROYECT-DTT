package org.dtt.mscatalog.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.dtt.mscatalog.application.port.out.ReservationItemStockRepositoryPort;
import org.dtt.mscatalog.domain.model.ReservationItemStock;
import org.dtt.mscatalog.infrastructure.mapper.ReservationStockMapper;
import org.dtt.mscatalog.infrastructure.persistence.entity.ReservationItemStockEntity;
import org.dtt.mscatalog.infrastructure.persistence.repository.SpringDataReservationItemStockRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReservationItemStockRepositoryAdapter implements ReservationItemStockRepositoryPort {

    private final SpringDataReservationItemStockRepository springDataReservationItemStockRepository;
    private final ReservationStockMapper reservationStockMapper;

    @Override
    public ReservationItemStock save(ReservationItemStock reservationItemStock) {
        ReservationItemStockEntity entity = reservationStockMapper.toEntity(reservationItemStock);
        entity = springDataReservationItemStockRepository.save(entity);
        return reservationStockMapper.toDomain(entity);
    }

    @Override
    public Optional<ReservationItemStock> findById(UUID id) {
        return springDataReservationItemStockRepository.findById(id)
                .map(reservationStockMapper::toDomain);
    }

    @Override
    public List<ReservationItemStock> saveAll(List<ReservationItemStock> reservationItemStocks) {
        List<ReservationItemStockEntity> entities = reservationStockMapper.toEntity(reservationItemStocks);
        entities = springDataReservationItemStockRepository.saveAll(entities);
        return reservationStockMapper.toDomain(entities);
    }
}
