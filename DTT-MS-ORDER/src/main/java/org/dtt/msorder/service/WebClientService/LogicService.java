package org.dtt.msorder.service.WebClientService;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dtt.msorder.client.WebClientLogic;
import org.dtt.msorder.dto.Request.ItemRequest;
import org.dtt.msorder.dto.Request.ReservationRequest;
import org.dtt.msorder.dto.Response.ItemCatalogResponse;
import org.dtt.msorder.dto.Response.ItemOrderResponse;
import org.dtt.msorder.dto.Response.ReservationResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogicService {


    private final WebClientLogic webClientLogic;

    public ReservationResponse productReservation(ReservationRequest reservationRequest,String token) throws Exception{
        return webClientLogic.reservationProducts(reservationRequest,token);
    }

    public void cancelReservation(UUID reservationId,String token) {
        webClientLogic.processReservation(reservationId, false,token);
    }       

    public void confirmReservation(UUID reservationId,String token) {
        webClientLogic.processReservation(reservationId, true,token);
    }

    public List<ItemOrderResponse> getItemsFromReservation(Map<UUID, List<UUID>> uuidListMap,String token){
        return webClientLogic.getItemsFromReservation(uuidListMap,token);
    }

}
