package org.dtt.msorder.service.WebClientService;
import java.util.List;
import java.util.UUID;

import org.dtt.msorder.client.CatalogClient;
import org.dtt.msorder.dto.Request.ReservationRequest;
import org.dtt.msorder.dto.Response.ItemOrderResponse;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogicService {

    private final CatalogClient catalogClient;

    public ReservationResponse productReservation(ReservationRequest reservationRequest) throws Exception {
        return catalogClient.createReservation(reservationRequest).data();
    }

    public void cancelReservation(UUID reservationId) {
        catalogClient.processReservation(reservationId, false);
    }

    public void confirmReservation(UUID reservationId) {
        catalogClient.processReservation(reservationId, true);
    }

    public List<ItemOrderResponse> getItemsFromReservation(UUID orderId) {
        return catalogClient.getProductByOrderId(orderId).data();
    }

}