package com.example.demo.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderRetrieve {
    private String orderNumber;
    private String userName;
    private String phoneNumber;
    private String productCode;
    private String productName;
    private String productId;
}
