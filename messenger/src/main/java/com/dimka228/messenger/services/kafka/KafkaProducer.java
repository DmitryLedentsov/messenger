package com.dimka228.messenger.services.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.services.interfaces.NotificationDeliveryService;

import lombok.AllArgsConstructor;

//@Service
@AllArgsConstructor
public class KafkaProducer implements NotificationDeliveryService {

	private final KafkaTemplate<String, Object> simpleProducer;

	@Override
	@Async
	public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO) {
		simpleProducer.send("messages-update", new KafkaMessageDTO(chatId,operationDTO));
		simpleProducer.flush();
	};

	@Override
	@Async
	public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO) {
		simpleProducer.send("chats-update", new KafkaChatDTO(userId, operationDTO));
		simpleProducer.flush();
	};

	@Override
	@Async
	public void sendNotificationToUser(Integer userId, NotificationDTO notificationDTO){
		simpleProducer.send("notifications-update", KafkaNotificationDTO.from(userId, notificationDTO));
		simpleProducer.flush();
	};

}