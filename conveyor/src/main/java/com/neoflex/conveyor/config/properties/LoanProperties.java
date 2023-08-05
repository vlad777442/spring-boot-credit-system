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
@ConfigurationProperties(prefix = "service.loan")
public class LoanProperties {
    private BigDecimal baseInterestRate;
    private Integer minLoanAge;
    private Integer annualPeriod;
    private Integer scale;
    private RoundingMode roundingMode;
    private BigDecimal salaryClientDiscount;
    private BigDecimal nonSalaryClientAdd;
    private BigDecimal insuranceClientDiscount;
    private BigDecimal nonInsuranceClientAdd;
}
