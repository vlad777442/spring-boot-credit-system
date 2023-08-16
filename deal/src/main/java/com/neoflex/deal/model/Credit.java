package com.neoflex.deal.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.neoflex.deal.dto.api.response.PaymentScheduleElement;
import com.neoflex.deal.model.enums.CreditStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "credit")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "term")
    private Integer term;

    @Column(name = "monthly_payment")
    private BigDecimal monthlyPayment;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "psk")
    private BigDecimal psk;

    @Column(name = "payment_schedule")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<PaymentScheduleElement> paymentSchedule;

    @Column(name = "insurance_enable")
    private Boolean isInsuranceEnabled;

    @Column(name = "salary_client")
    private Boolean isSalaryClient;

    @Column(name = "credit_status")
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}
