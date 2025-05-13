package com.dimka228.messenger.services;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.services.interfaces.NotificationDeliveryService;

import lombok.AllArgsConstructor;

//@Service
@AllArgsConstructor
public class SocketMessagingService implements NotificationDeliveryService {

	private final SimpMessagingTemplate msgTemplate;

	@Override
	@Async
	public void sendMessageOperationToChat(Integer chatId, OperationDTO<MessageDTO> operationDTO) {
		msgTemplate.convertAndSend("/topic/chat/" + chatId + "/messages", operationDTO);
	}

	@Override
	@Async
	public void sendChatOperationToUser(Integer userId, OperationDTO<ChatDTO> operationDTO) {
		msgTemplate.convertAndSend("/topic/user/" + userId + "/chats", operationDTO);
	}
	@Override
	@Async
	public void sendNotificationToUser(Integer userId, NotificationDTO notificationDTO){
		msgTemplate.convertAndSend("/topic/user/" + userId + "/notifications", notificationDTO);
	};


}
