package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.PaymentScheduleElement;
import com.neoflex.conveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CreditService {
    CreditDTO calculateCreditDetails(ScoringDataDTO scoringData);
    BigDecimal calculateInterestRate(ScoringDataDTO scoringData);
    BigDecimal calculateCreditRateByPosition(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculateCreditRateByMaritalStatus(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculateCreditRateByDependents(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculateCreditRateByAge(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculatePSK(ScoringDataDTO scoringData, BigDecimal interestRate);
    BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringData, BigDecimal interestRate);

}
