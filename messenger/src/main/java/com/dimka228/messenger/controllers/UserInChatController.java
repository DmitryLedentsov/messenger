package com.dimka228.messenger.controllers;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dimka228.messenger.dto.ChatDTO;
import com.dimka228.messenger.dto.OperationDTO;
import com.dimka228.messenger.dto.UserInChatDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.entities.UserInChat;
import com.dimka228.messenger.exceptions.CannotBanSelfException;
import com.dimka228.messenger.exceptions.WrongPrivilegesException;
import com.dimka228.messenger.services.ChatService;
import com.dimka228.messenger.services.RoleService;
import com.dimka228.messenger.services.UserService;
import com.dimka228.messenger.services.interfaces.NotificationDeliveryService;
import com.dimka228.messenger.utils.DateConverter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
public class UserInChatController {

	private final UserService userService;

	private final ChatService chatService;
	private final RoleService roleService;

	@Qualifier("notificationDeliveryService")
	private final NotificationDeliveryService notificationService;

	@GetMapping("/chat/{chatId}/user/{id}")
	public UserInChatDTO profile(@PathVariable Integer chatId, @PathVariable Integer id) {
		User user = userService.getUser(id);
		Chat chat = chatService.getChat(chatId);
		Set<String> userStatuses = userService.getUserStatusNames(user);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		UserInChatDTO profileDTO = new UserInChatDTO(user.getLogin(), userInChat.getRole().getName(), user.getId(),
				userStatuses, DateConverter.format(userInChat.getJoinTime()));

		return profileDTO;
	}
	@GetMapping("/chat/{chatId}/user-by-login/{name}")
	public UserInChatDTO profileByLogin(@PathVariable Integer chatId, @PathVariable String name) {
		User user = userService.getUser(name);
		Chat chat = chatService.getChat(chatId);
		Set<String> userStatuses = userService.getUserStatusNames(user);
		UserInChat userInChat = chatService.getUserInChat(user, chat);
		UserInChatDTO profileDTO = new UserInChatDTO(user.getLogin(), userInChat.getRole().getName(), user.getId(),
				userStatuses, DateConverter.format(userInChat.getJoinTime()));

		return profileDTO;
	}
	@DeleteMapping("/chat/{chatId}/user/{userId}")
	public void banUser(@PathVariable Integer chatId, @PathVariable Integer userId, Principal principal) {
		User cur = userService.getUser(principal.getName());
		Chat chat = chatService.getChat(chatId);
		User user = userService.getUser(userId);
		UserInChat userInChat = chatService.getUserInChat(cur, chat);

		if (!userInChat.getRole().isBanUser())
			throw new WrongPrivilegesException();
		if (!roleService.isHigherPriority(userInChat, chatService.getUserInChat(user, chat)))
			throw new WrongPrivilegesException();
		if (Objects.equals(user.getId(), cur.getId()))
			throw new CannotBanSelfException();
	
		chatService.deleteOrLeaveChat(chatService.getUserInChat(user, chat));
		notificationService.sendChatOperationToUser(userId, new OperationDTO<>(new ChatDTO(chatId), OperationDTO.DELETE));
		/*List<MessageInfo> messages = chatService.getMessagesForUserInChat(user, chat);

		
		for (MessageInfo messageInfo : messages) {
			MessageDTO data = new MessageDTO(messageInfo.getId(), messageInfo.getMessage(), userId, user.getLogin(),
					null);
			OperationDTO<MessageDTO> op = new OperationDTO<>(data, OperationDTO.DELETE);
			notificationService.sendMessageOperationToChat(chatId, op);
		}*/
	}

	@PostMapping("/chat/{chatId}/user/{login}")
	public UserInChatDTO addUserInChat(@PathVariable Integer chatId, @PathVariable String login, Principal principal) {
		User user = userService.getUser(login);
		Chat chat = chatService.getChat(chatId);
		User cur = userService.getUser(principal.getName());
		UserInChat userInChat = chatService.getUserInChat(cur, chat);
		if (!userInChat.getRole().isAddUser()) throw new WrongPrivilegesException();
		chatService.addUserInChat(user, chat, roleService.getRole(UserInChat.Roles.REGULAR));

		ChatDTO chatDTO = new ChatDTO(chat.getId(), chat.getName(), UserInChat.Roles.REGULAR);
		OperationDTO<ChatDTO> data = new OperationDTO<>(chatDTO, OperationDTO.ADD);
		notificationService.sendChatOperationToUser(user.getId(), data);
		
		return new UserInChatDTO(user.getLogin(), UserInChat.Roles.REGULAR, user.getId(),
				userService.getUserStatusNames(user), DateConverter.format(chatService.getUserInChat(user, chat).getJoinTime()));
	}

	@GetMapping("/chat/{chatId}/users")
	public List<UserInChatDTO> profiles(@PathVariable Integer chatId, Principal principal,  @RequestParam(defaultValue = "0", name="page") int pageNumber, @RequestParam(defaultValue = "100", name="count") int pageSize,@RequestParam(required= false ,value="filter") String filter) {

		List<UserInChatDTO> profiles = new LinkedList<>();
		Chat chat = chatService.getChat(chatId);
		UserInChat curUserInChat = chatService.getUserInChat(userService.getUser(principal.getName()), chat);

		Pageable page = PageRequest.of(pageNumber, pageSize, Sort.by("joinTime").descending());
		List<UserInChat> usersInChat = chatService.getUsersInChat(chat,page).stream()
		.filter((u)->filter!=null?u.getUser().getLogin().contains(filter):true)
		.toList();
		for (UserInChat userInChat :usersInChat) {
			if (userInChat.getUser().equals(curUserInChat.getUser())) continue; //себя пропускаем
			Set<String> userStatuses = userService.getUserStatusNames(userInChat.getUser());
			UserInChatDTO profileDTO = new UserInChatDTO(userInChat.getUser().getLogin(), userInChat.getRole().getName(),
					userInChat.getUser().getId(), userStatuses, DateConverter.format(userInChat.getJoinTime()));
			profiles.add(profileDTO);

		}
		return profiles;
	}


}
