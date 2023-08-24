package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.api.response.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    /**
     * This method returns 4 loan offers using different parameters combinations
     * @param requestDTO
     * @return
     */
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO);

    /**
     * This method generates loan offer based on LoanApplicationRequestDTO and other parameters
     * @param isInsuranceEnabled
     * @param isSalaryClient
     * @param requestDTO
     * @return
     */
    LoanOfferDTO generateLoanOffer(boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO requestDTO);
    BigDecimal calculateLoanRateBySalaryClient(BigDecimal interestRate, boolean isSalaryClient);
    BigDecimal calculateLoanRateByInsuranceClient(BigDecimal interestRate, boolean isInsuranceEnabled);
    BigDecimal calculateLoanMonthlyPayment(BigDecimal totalAmount, Integer term);
}
