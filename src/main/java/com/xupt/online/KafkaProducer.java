package com.xupt.online;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KafkaProducer {
    @Resource
    private KafkaTemplate template;
}
