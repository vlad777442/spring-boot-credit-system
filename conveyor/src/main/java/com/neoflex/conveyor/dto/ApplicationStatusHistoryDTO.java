package com.neoflex.conveyor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationStatusHistoryDTO {
    private ApplicationStatusType status;
    private LocalDateTime time;
    private ChangeType changeType;
}
