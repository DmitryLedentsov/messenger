package com.dimka228.messenger.services;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.entities.Chat;
import com.dimka228.messenger.entities.Notification;
import com.dimka228.messenger.entities.User;
import com.dimka228.messenger.exceptions.ChatNotFoundException;
import com.dimka228.messenger.exceptions.NotificationNotForUserException;
import com.dimka228.messenger.exceptions.NotificationNotFoundException;
import com.dimka228.messenger.repositories.NotificationRepository;

import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public Notification addNotification(User user, Chat chat, String type, String text) {
        Notification notification = new Notification();
        notification.setChat(chat);
        notification.setUser(user);
        notification.setData(text);
        notification.setType(type);
		return notificationRepository.save(notification);
	}

    @Transactional
    public void clearNotifications(User user){
        notificationRepository.deleteByUserId(user.getId());
    }

   
    public List<Notification> getNotifications(User user){
        return notificationRepository.findAllByUserId(user.getId());
    }
    
    public List<Notification> getNotifications(User user, Pageable page){
        return notificationRepository.findAllByUserId(user.getId(), page);
    }
    public List<Notification> getNotifications(User user, String type, Pageable page){
        return notificationRepository.findByUserIdAndType(user.getId(), type, page);
    }

    public Notification getNotification(Integer id) {
		try {
			return notificationRepository.findById(id).orElseThrow(() -> new NotificationNotFoundException());
		}
		catch (EntityNotFoundException e) {
			throw new NotificationNotFoundException();
		}
	}
    @Transactional
	public void deleteNotification(User user, Notification notification) {
        if(!Objects.equals(user.getId(),notification.getUser().getId())) throw new NotificationNotForUserException();
		notificationRepository.deleteById(notification.getId());
	}
}
