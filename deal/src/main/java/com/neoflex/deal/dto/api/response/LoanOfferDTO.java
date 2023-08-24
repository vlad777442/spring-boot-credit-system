package com.neoflex.deal.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Jacksonized
@Schema(description = "Loan offer dto")
public class LoanOfferDTO {
    @Schema(description = "Application ID", example = "12345")
    private Long applicationId;

    @Schema(description = "Requested loan amount", example = "10000.00")
    @DecimalMin(value = "0.01", message = "Requested amount must be greater than 0")
    private BigDecimal requestedAmount;

    @Schema(description = "Total amount to be repaid", example = "11000.00")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than 0")
    private BigDecimal totalAmount;

    @Schema(description = "Loan term (in months)", example = "12")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term;

    @Schema(description = "Monthly loan payment", example = "916.67")
    @DecimalMin(value = "0.01", message = "Monthly payment must be greater than 0")
    private BigDecimal monthlyPayment;

    @Schema(description = "Loan interest rate", example = "0.1")
    @DecimalMin(value = "0.001", message = "Interest rate must be greater than 0")
    private BigDecimal rate;

    @Schema(description = "Insurance status", example = "true")
    @NotNull(message = "Insurance status must not be null")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Salary client status", example = "false")
    @NotNull(message = "Salary client status must not be null")
    private Boolean isSalaryClient;
}
