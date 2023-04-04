package org.userrequsts.dto;

import lombok.Data;
import org.userrequsts.model.RequestStatus;

import java.time.LocalDateTime;

@Data
public class UserRequestDto {

    private Long id;
    private String message;
    private RequestStatus status;
    private LocalDateTime createdDate;
}
