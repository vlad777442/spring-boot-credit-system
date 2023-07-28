package com.neoflex.conveyor.dto;

import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private EmailThemeType theme;
    private Long applicationId;
}
