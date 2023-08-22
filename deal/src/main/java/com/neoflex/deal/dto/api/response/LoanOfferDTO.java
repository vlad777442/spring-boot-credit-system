package com.neoflex.deal.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Loan offer dto")
public class LoanOfferDTO {
    @Schema(description = "Application ID")
    private Long applicationId;

    @Schema(description = "Requested loan amount")
    private BigDecimal requestedAmount;

    @Schema(description = "Total amount to be repaid")
    private BigDecimal totalAmount;

    @Schema(description = "Loan term (in months)")
    private Integer term;

    @Schema(description = "Monthly loan payment")
    private BigDecimal monthlyPayment;

    @Schema(description = "Loan interest rate")
    private BigDecimal rate;

    @Schema(description = "Insurance status")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Salary client status")
    private Boolean isSalaryClient;
}
