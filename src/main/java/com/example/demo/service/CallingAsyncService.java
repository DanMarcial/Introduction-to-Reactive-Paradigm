package com.example.demo.service;

import com.example.demo.data.Order;
import com.example.demo.utils.MDCContextLifter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

@Service
@RequiredArgsConstructor
public class CallingAsyncService {
    private final WebClient webClient;
    private static final Logger log = LoggerFactory.getLogger(CallingAsyncService.class);

    public CallingAsyncService() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081/orderSearchService")
                .build();
    }

    public Flux<Order> getDataStream(String phoneNumber, String requestId) {
        log.info("Fetching order data for phone number: {}", phoneNumber); // Initial log

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/order/phone")
                        .queryParam("phoneNumber", phoneNumber)
                        .build())
                .accept(MediaType.valueOf(MediaType.APPLICATION_NDJSON_VALUE))
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnNext(order -> log.info("Received order: {}", order.getOrderNumber()))
                .doOnError(error -> log.error("Failed to fetch order data: {}", error.getMessage()))
                .doOnComplete(() -> log.info("Completed fetching orders for {}", phoneNumber))
                .transform(MDCContextLifter.lift())
                .contextWrite(Context.of(
                        "requestId", requestId
                ));
    }


}
