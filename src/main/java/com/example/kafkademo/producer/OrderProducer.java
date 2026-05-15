package com.example.kafkademo.producer;

import com.example.kafkademo.avro.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${topic.name}")
    private String topicName;

    public OrderProducer(KafkaTemplate<String, OrderEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(OrderEvent event) {
        kafkaTemplate.send(topicName, event.getOrderId().toString(),event);

        //System.out.println("Message Sent: " + message);
        log.info("Message Sent: {}", event);
        log.debug("DEBUG Message Sent: {}", event);
    }
}