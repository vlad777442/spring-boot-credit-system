package com.neoflex.conveyor.dto;

import com.neoflex.conveyor.dto.enumType.EmailThemeType;
import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private EmailThemeType theme;
    private Long applicationId;
}
