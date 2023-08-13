package com.neoflex.deal.dto.api.request;

import com.neoflex.deal.dto.enums.EmailThemeType;
import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private EmailThemeType theme;
    private Long applicationId;
}
