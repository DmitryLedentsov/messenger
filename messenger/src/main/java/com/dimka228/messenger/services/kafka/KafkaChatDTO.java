package com.dimka228.messenger.services.kafka;
import com.dimka228.messenger.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaChatDTO extends OperationDTO<ChatDTO> {
    private Integer userId;

    public KafkaChatDTO(Integer userId, OperationDTO<ChatDTO> op){
        super(op.getData(), op.getOperation());
        this.userId=userId;
    }
}
