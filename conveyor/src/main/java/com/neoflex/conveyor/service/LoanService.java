package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.List;

public interface LoanService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO);
    LoanOfferDTO generateLoanOffer(Long id, boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO requestDTO);
    BigDecimal calculateLoanRateBySalaryClient(BigDecimal interestRate, boolean isSalaryClient);
    BigDecimal calculateLoanRateByInsuranceClient(BigDecimal interestRate, boolean isInsuranceEnabled);
    BigDecimal calculateLoanMonthlyPayment(BigDecimal totalAmount, Integer term);
}
