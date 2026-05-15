package com.example.kafkademo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    private String orderId;
    private String customerId;
    private String status;
    private String priority;

}
