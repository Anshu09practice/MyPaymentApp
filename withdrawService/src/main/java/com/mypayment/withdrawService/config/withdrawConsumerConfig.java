package com.mypayment.withdrawService.config;

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

@EnableKafka
@Configuration
public class withdrawConsumerConfig {
    @Bean
    public ConsumerFactory<String, Object> consumerFactorywithdraw() {
        Map<String, Object> configwithdraw = new HashMap<>();
        configwithdraw.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configwithdraw.put(ConsumerConfig.GROUP_ID_CONFIG, "withdraw-account-group");
        configwithdraw.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configwithdraw.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class.getName());
        configwithdraw.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        configwithdraw.put(JsonDeserializer.TRUSTED_PACKAGES, "com.mypayment.withdrawService.*");

        return new DefaultKafkaConsumerFactory<>(configwithdraw);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> withdrawkafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactorywithdraw());
        return factory;
    }
}
