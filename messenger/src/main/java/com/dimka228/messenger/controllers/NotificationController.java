package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.NotificationCreateDTO;
import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.Notification;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.NotificationService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.services.interfaces.NotificationDeliveryService;
import com.dimka228.messenger.utils.DateConverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class NotificationController {
    private final UserService userService;
    @Qualifier("notificationDeliveryService")
	private final NotificationDeliveryService notificationDeliveryService;

    private final ChatService chatService;

	private final NotificationService notificationService;

    @PostMapping("/chat/{chatId}/user/{userId}/notifications")
	public void addNotification(@PathVariable Integer chatId, @PathVariable Integer userId, @RequestBody NotificationCreateDTO notificationCreateDTO, Principal principal) {
		User cur = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		UserInChat curInChat = chatService.getUserInChat(cur, chat);
		if(!curInChat.getRole().isSendNotification()) throw new WrongPrivilegesException();
		
		User user = userService.getUser(userId);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		Notification notification = notificationService.addNotification(user, chat, notificationCreateDTO.getType(), notificationCreateDTO.getData());
		NotificationDTO notificationDTO = new NotificationDTO(notification.getId(), chat.getId(), notificationCreateDTO.getData(), DateConverter.format(Instant.now()), notificationCreateDTO.getType());

		notificationDeliveryService.sendNotificationToUser(userId, notificationDTO);
	}

	@GetMapping("/notifications")
	public List<NotificationDTO> getNotifications(Principal principal, @RequestParam(defaultValue = "0", name="page") int pageNumber, @RequestParam(defaultValue = "100", name="count") int pageSize, @RequestParam(required= false ,value="type") String type, @RequestParam(required= false ,value="filter") String filter) {
		User cur = userService.getUser(principal.getName());
		Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by("sendTime").descending());
		List<Notification> notifications = type==null?notificationService.getNotifications(cur,page):notificationService.getNotifications(cur,type,page);
		return notifications.stream()
			.filter((m)->filter!=null?m.getData().contains(filter):true)
			.map(Notification::toDto)
			.toList();
	}

	@DeleteMapping("/notifications")
	public void clear(Principal principal) {
		User cur = userService.getUser(principal.getName());
		notificationService.clearNotifications(cur);
	}
	@DeleteMapping("/notification/{id}")
	public void delete(@PathVariable Integer id, Principal principal) {
		User cur = userService.getUser(principal.getName());
		notificationService.deleteNotification(cur, notificationService.getNotification(id));
	}
}
