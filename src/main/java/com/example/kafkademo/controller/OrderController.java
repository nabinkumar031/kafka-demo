package com.example.kafkademo.controller;


import com.example.kafkademo.avro.OrderEvent;
import com.example.kafkademo.dto.OrderRequest;
import com.example.kafkademo.producer.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private  OrderProducer producer;

    @PostMapping
    public String publish(@RequestBody OrderRequest request) {

        OrderEvent orderEvent = OrderEvent.newBuilder()
                .setOrderId(request.getOrderId())
                .setCustomerId(request.getCustomerId())
                .setStatus(request.getStatus())
                .setPriority(request.getPriority())
                .build();

        producer.sendMessage(orderEvent);

        return "Version 2 : Order sent to Kafka";
    }
}