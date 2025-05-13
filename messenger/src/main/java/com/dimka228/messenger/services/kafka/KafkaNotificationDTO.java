package com.dimka228.messenger.services.kafka;

import com.dimka228.messenger.dto.NotificationDTO;
import com.dimka228.messenger.utils.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaNotificationDTO extends NotificationDTO {
    private Integer userId;

    public static KafkaNotificationDTO from(Integer chatId,NotificationDTO dto){
        KafkaNotificationDTO kafkaDTO = ObjectMapper.map(dto, KafkaNotificationDTO.class);
        kafkaDTO.setUserId(chatId);
        return kafkaDTO;
    }
}
