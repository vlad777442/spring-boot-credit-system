package com.neoflex.gateway.model;

import com.neoflex.gateway.dto.api.response.PaymentScheduleElement;
import com.neoflex.gateway.model.enums.CreditStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Credit {
    private Long creditId;

    private BigDecimal amount;

    private Integer term;

    private BigDecimal monthlyPayment;

    private BigDecimal rate;

    private BigDecimal psk;

    private List<PaymentScheduleElement> paymentSchedule;

    private Boolean isInsuranceEnabled;

    private Boolean isSalaryClient;

    private CreditStatus creditStatus;
}
