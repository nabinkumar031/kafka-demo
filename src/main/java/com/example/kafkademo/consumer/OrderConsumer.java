package com.example.kafkademo.consumer;

import com.example.kafkademo.avro.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderConsumer {

    @Value("${instance.name}")
    private String instanceName;

    @KafkaListener(topics = "${topic.name}", groupId = "order-group")
    public void consume(OrderEvent message) {

        log.info(
                "Instance: {} received message: {}",
                instanceName,
                message
        );

        //System.out.println("Message Received: " + message);
        //log.info("Message Received: {}", message);
        //log.debug("DEBUG Message Received: {}", message);

        //throw new RuntimeException("Simulate Error");
    }
}