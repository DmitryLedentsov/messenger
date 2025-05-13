package com.dimka228.messenger.services.kafka;

import com.dimka228.messenger.dto.MessageDTO;
import com.dimka228.messenger.dto.OperationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaMessageDTO extends OperationDTO<MessageDTO> {
    private Integer chatId;

    public KafkaMessageDTO(Integer chatId, OperationDTO<MessageDTO> op){
        super(op.getData(), op.getOperation());
        this.chatId=chatId;
    }
}
