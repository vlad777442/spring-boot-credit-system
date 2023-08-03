package com.neoflex.conveyor.dto.api.response;

import com.neoflex.conveyor.dto.enums.ApplicationStatusType;
import com.neoflex.conveyor.dto.enums.ChangeType;
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
