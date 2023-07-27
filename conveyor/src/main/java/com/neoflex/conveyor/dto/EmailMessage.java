package com.neoflex.conveyor.dto;

import lombok.Data;

@Data
public class EmailMessage {
    private String address;
    private Enum theme;
    private Long applicationId;
}
