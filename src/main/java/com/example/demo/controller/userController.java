package com.example.demo.controller;

import com.example.demo.data.Order;
import com.example.demo.data.OrderRetrieve;
import com.example.demo.data.Product;
import com.example.demo.data.user;
import com.example.demo.interfaces.ItemRepository;
import com.example.demo.service.CallingAsyncService;
import com.example.demo.service.CallingSyncService;
import com.example.demo.service.GeneralService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/items")
public class userController {
    private final ItemRepository itemRepository;
    private final CallingAsyncService callingAsyncService;
    private final CallingSyncService callingSyncService;
    private final GeneralService generalService;

    public userController(ItemRepository itemRepository, CallingAsyncService CallingAsyncService, CallingSyncService callingSyncService, GeneralService generalService) {
        this.itemRepository = itemRepository;
        this.callingAsyncService = CallingAsyncService;
        this.callingSyncService = callingSyncService;
        this.generalService = generalService;
    }

    @GetMapping(value = "/all")
    public List<user> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping(value = "/order/phone")
    public Flux<Order> getOrderByPhone(@RequestParam String phoneNumber,
                                       @RequestHeader("X-Request-ID") String requestId) {
        return callingAsyncService.getDataStream(phoneNumber, requestId);
    }

    @GetMapping(value = "/order/product")
    public Mono<Product> getOrderByProduct(@RequestParam String productName,
                                           @RequestHeader("X-Request-ID") String requestId) {
        return callingSyncService.getProductNames(productName, requestId);
    }

    @GetMapping(value = "/order/retrieve")
    public Flux<OrderRetrieve> getOrderRetrieve(@RequestParam String phoneNumber,
                                                @RequestHeader("X-Request-ID") String requestId) {
        return generalService.orderRetrieve(phoneNumber, requestId);
    }

}
