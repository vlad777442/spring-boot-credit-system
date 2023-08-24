package com.neoflex.application.dto.api.request;


import com.neoflex.application.dto.enums.EmailThemeType;
import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private EmailThemeType theme;
    private Long applicationId;
}
