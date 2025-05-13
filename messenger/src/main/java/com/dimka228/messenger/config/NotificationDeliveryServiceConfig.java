package com.dimka228.messenger.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.dimka228.messenger.services.SocketMessagingService;
import com.dimka228.messenger.services.interfaces.NotificationDeliveryService;
import com.dimka228.messenger.services.kafka.KafkaProducer;

@Configuration
public class NotificationDeliveryServiceConfig {

	@Bean(name = "notificationDeliveryService")
	@ConditionalOnProperty(name = "messenger.multi-instance", havingValue = "true")
	public NotificationDeliveryService kafkaProducer(KafkaTemplate<String, Object> tmp) {
		return new KafkaProducer(tmp);
	}

	@Bean(name = "notificationDeliveryService")
	@ConditionalOnProperty(name = "messenger.multi-instance", havingValue = "false", matchIfMissing = true)
	public NotificationDeliveryService socketMessagingService(SimpMessagingTemplate tmp) {
		return new SocketMessagingService(tmp);
	}

	@Bean
	public SocketMessagingService kafkaSocketMessagingService(SimpMessagingTemplate tmp) {
		return new SocketMessagingService(tmp);
	}

}