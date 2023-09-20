package com.neoflex.deal.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "credit")
public class DealProperties {
    @NotNull
    private BigDecimal minRate;

    @NotNull
    private BigDecimal maxRate;
}
