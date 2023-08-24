package com.neoflex.application.dto.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@Jacksonized
@Schema(description = "Credit dto")
public class CreditDTO {
    @Schema(description = "Loan amount")
    private BigDecimal amount;

    @Schema(description = "Loan term (in months)")
    private Integer term;

    @Schema(description = "Monthly loan payment")
    private BigDecimal monthlyPayment;

    @Schema(description = "Loan interest rate")
    private BigDecimal rate;

    @Schema(description = "Total cost of the loan (PSK)")
    private BigDecimal psk;

    @Schema(description = "Insurance status")
    private Boolean isInsuranceEnabled;

    @Schema(description = "Salary client status")
    private Boolean isSalaryClient;

    @Schema(description = "Payment schedule")
    private List<PaymentScheduleElement> paymentSchedule;
}
