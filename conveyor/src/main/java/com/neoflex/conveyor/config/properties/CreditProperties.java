package com.neoflex.conveyor.config.properties;

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
    private BigDecimal baseInterestRate;
    private Integer annualPeriod;
    private Integer minSalaryCount;
    private Integer minCreditAge;
    private Integer maxCreditAge;
    private Integer minTotalWorkExperience;
    private Integer minCurrentWorkExperience;
    private Integer scale;
    private RoundingMode roundingMode;
    private BigDecimal middlePositionDiscount;
    private BigDecimal seniorPositionDiscount;
    private BigDecimal marriedDiscount;
    private BigDecimal divorcedAdd;
    private BigDecimal dependentsAdd;
    private BigDecimal femaleBetween35And60Discount;
    private BigDecimal maleBetween30And55Discount;
    private BigDecimal nonBinaryAdd;
}
