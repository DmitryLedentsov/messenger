
package com.dimka228.messenger.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.utils.DateConverter;

@Entity
@Table(name = "m_notification")
@Data
public class Notification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "chat_id", nullable = true)
	private Chat chat;

	@Column(name = "data", nullable = true)
	private String data;

    @Column(name = "type", nullable = false)
	private String type;

	@Column(name = "send_time")
	private Instant sendTime;

	
    public static class Types {

		public static final String MENTION = "MENTION";

	}

	public NotificationDTO toDto(){
		return new NotificationDTO(id,chat.getId(),data,DateConverter.format(getSendTime()),type);
	}
}
