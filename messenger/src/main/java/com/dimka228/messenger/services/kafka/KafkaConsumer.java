package com.dimka228.messenger.services.kafka;

import com.dimka228.messenger.services.SocketMessagingService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.dto.OperationDTO;

@Component
@ConditionalOnProperty(name = "messenger.multi-instance")
@AllArgsConstructor
@Slf4j
public class KafkaConsumer {

	private final SocketMessagingService socketMessagingService;

	@KafkaListener(topics = "chats-update")
	public void updateChats(KafkaChatDTO message) {
		log.info("Received chats update: {}", message);
		socketMessagingService.sendChatOperationToUser(message.getUserId(), message);
	}

	@KafkaListener(topics = "messages-update")
	public void updateChat(KafkaMessageDTO message) {
		log.info("Received messages update: {}", message);
		socketMessagingService.sendMessageOperationToChat(message.getChatId(), message);
	}

	@KafkaListener(topics = "notifications-update")
	public void updateNotifications(KafkaNotificationDTO message) {
		log.info("Received notification update: {}", message);
		socketMessagingService.sendNotificationToUser(message.getUserId(), message);
	}

}