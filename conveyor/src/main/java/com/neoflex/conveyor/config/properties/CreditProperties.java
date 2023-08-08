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
@ConfigurationProperties(prefix = "service.credit")
public class CreditProperties {
    @NotNull
    private BigDecimal baseInterestRate;

    @NotNull
    private Integer annualPeriod;

    @NotNull
    private Integer minSalaryCount;

    @NotNull
    private Integer minCreditAge;

    @NotNull
    private Integer maxCreditAge;

    @NotNull
    private Integer minTotalWorkExperience;

    @NotNull
    private Integer minCurrentWorkExperience;

    @NotNull
    private Integer scale;

    @NotNull
    private RoundingMode roundingMode;

    @NotNull
    private BigDecimal middlePositionDiscount;

    @NotNull
    private BigDecimal seniorPositionDiscount;

    @NotNull
    private BigDecimal marriedDiscount;

    @NotNull
    private BigDecimal divorcedAdd;

    @NotNull
    private BigDecimal dependentsAdd;

    @NotNull
    private BigDecimal femaleBetween35And60Discount;

    @NotNull
    private BigDecimal maleBetween30And55Discount;

    @NotNull
    private BigDecimal nonBinaryAdd;
}
