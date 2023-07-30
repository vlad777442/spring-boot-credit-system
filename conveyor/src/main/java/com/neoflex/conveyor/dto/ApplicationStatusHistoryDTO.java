package com.neoflex.conveyor.dto;

import com.neoflex.conveyor.dto.enumType.ApplicationStatusType;
import com.neoflex.conveyor.dto.enumType.ChangeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Application history dto")
public class ApplicationStatusHistoryDTO {
    private ApplicationStatusType status;
    private LocalDateTime time;
    private ChangeType changeType;
}
