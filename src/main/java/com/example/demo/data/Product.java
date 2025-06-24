package com.example.demo.data;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {
    private String productId;
    private String productCode;
    private String productName;
    private double score;
}
