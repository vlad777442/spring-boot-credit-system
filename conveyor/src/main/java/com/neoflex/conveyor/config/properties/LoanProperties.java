package com.neoflex.conveyor.config.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "service.loan")
public class LoanProperties {
    @NotNull
    private BigDecimal baseInterestRate;

    @NotNull
    private Integer minLoanAge;

    @NotNull
    private Integer annualPeriod;

    @NotNull
    private Integer scale;

    @NotNull
    private RoundingMode roundingMode;

    @NotNull
    private BigDecimal salaryClientDiscount;

    @NotNull
    private BigDecimal nonSalaryClientAdd;

    @NotNull
    private BigDecimal insuranceClientDiscount;

    @NotNull
    private BigDecimal nonInsuranceClientAdd;
}
