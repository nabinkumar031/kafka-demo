package com.example.kafkademo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Configuration
public class KafkaErrorHandlerConfig {

    @Value("${kafka.dlq.topic}")
    private String dlqTopic;

    @Value("${kafka.retry.interval}")
    private long retryInterval;

    @Value("${kafka.retry.max-attempts}")
    private long maxAttempts;

   @Bean
    public DefaultErrorHandler errorHandler(
            KafkaTemplate<Object, Object> template) {

        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template,
                        (record, ex) -> {

                            log.error(
                                    "Sending message to DLQ. Topic: {}, Partition: {}, Error: {}",
                                    record.topic(),
                                    record.partition(),
                                    ex.getMessage()
                            );

                            return new TopicPartition(
                                    dlqTopic,
                                    record.partition()
                            );
                        });

        FixedBackOff fixedBackOff =
                new FixedBackOff(retryInterval, maxAttempts);

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        recoverer,
                        fixedBackOff
                );

        return errorHandler;
    }
}