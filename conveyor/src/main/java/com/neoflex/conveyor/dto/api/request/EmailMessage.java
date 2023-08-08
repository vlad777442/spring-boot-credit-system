package com.neoflex.conveyor.dto.api.request;

import com.neoflex.conveyor.dto.enums.EmailThemeType;
import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private EmailThemeType theme;
    private Long applicationId;
}
