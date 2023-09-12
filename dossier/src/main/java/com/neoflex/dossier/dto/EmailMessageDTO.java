package com.neoflex.dossier.dto;

import com.neoflex.dossier.dto.enums.EmailThemeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@Schema(description = "Email Message dto")
public class EmailMessageDTO {
    @Schema(description = "Email address", example = "test@gmail.com")
    private String address;

    @Schema(description = "Theme", example = "FINISH_REGISTRATION")
    private EmailThemeType theme;

    @Schema(description = "Application id", example = "1")
    private Long applicationId;
}
