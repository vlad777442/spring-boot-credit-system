package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.api.response.CreditDTO;
import com.neoflex.conveyor.dto.api.request.ScoringDataDTO;

import java.math.BigDecimal;

public interface CreditService {
    /**
     * This method calculates credit scoring based on ScoringDataDTO and returns credit details using CreditDTO
     * @param scoringData
     * @return
     */
    CreditDTO calculateCreditDetails(ScoringDataDTO scoringData);

    /**
     * This method calculates interest rate based on different parameters such as age, employment, and etc.
     * @param scoringData
     * @return
     */
    BigDecimal calculateInterestRate(ScoringDataDTO scoringData);
    BigDecimal calculateCreditRateByPosition(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculateCreditRateByMaritalStatus(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculateCreditRateByDependents(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculateCreditRateByAge(ScoringDataDTO scoringDataDTO, BigDecimal interestRate);
    BigDecimal calculatePSK(ScoringDataDTO scoringData, BigDecimal interestRate);
    BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringData, BigDecimal interestRate);

}
