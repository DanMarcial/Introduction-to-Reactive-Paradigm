package com.example.demo.service;

import com.example.demo.data.Product;
import com.example.demo.utils.MDCContextLifter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.util.context.Context;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CallingSyncService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(CallingSyncService.class);

    public CallingSyncService() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));  // Set timeout

        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8082")
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public Mono<Product> getProductNames(String productCode, String requestId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/productInfoService/product/names")
                        .queryParam("productCode", productCode)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(20))
                .onErrorResume(throwable -> {
                    log.error("Error fetching product names: {}", throwable.getMessage());
                    return Mono.just("[]");
                })
                .flatMap(jsonArray -> {
                    try {
                        List<Product> products = objectMapper.readValue(jsonArray, new TypeReference<>() {});
                        if (!products.isEmpty()) {
                            Product maxScoreProduct = Collections.max(products, Comparator.comparingDouble(Product::getScore));
                            log.info("Found max score product: {}", maxScoreProduct.getProductName());
                            return Mono.just(maxScoreProduct);
                        } else {
                            log.warn("Product list was empty for code {}", productCode);
                            return Mono.empty();
                        }
                    } catch (Exception e) {
                        log.error("Failed to deserialize product list", e);
                        return Mono.error(e);
                    }
                })
                .transform(MDCContextLifter.lift())
                .contextWrite(Context.of(
                        "requestId", requestId
                ));
    }

}
