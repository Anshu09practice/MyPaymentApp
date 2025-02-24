package com.mypayment.TransferService.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class transferConsumerConfig {
    @Bean
    public ConsumerFactory<String, Object> consumerFactorytransfer() {
        Map<String, Object> transferconfig = new HashMap<>();
        transferconfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        transferconfig.put(ConsumerConfig.GROUP_ID_CONFIG, "transfer-account-group");
        transferconfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        transferconfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        transferconfig.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        transferconfig.put(JsonDeserializer.TRUSTED_PACKAGES, "com.mypayment.TransferService.*");

        return new DefaultKafkaConsumerFactory<>(transferconfig);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> transferkafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactorytransfer());
        return factory;
    }
}
