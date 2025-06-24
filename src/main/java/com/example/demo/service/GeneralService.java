package com.example.demo.service;

import com.example.demo.data.OrderRetrieve;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class GeneralService {
    private final CallingAsyncService callingAsyncService;
    private final CallingSyncService callingSyncService;

    public Flux<OrderRetrieve> orderRetrieve(String phoneNumber, String requestId) {
        return callingAsyncService.getDataStream(phoneNumber, requestId)
                .flatMap(order ->
                        callingSyncService.getProductNames(order.getProductCode(), requestId)
                                .map(product -> new OrderRetrieve(
                                        order.getOrderNumber(),
                                        "UserName",
                                        order.getPhoneNumber(),
                                        order.getProductCode(),
                                        product.getProductName(),
                                        product.getProductId()
                                ))
                );
    }

}
