package com.neoflex.deal.service;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.Application;

import java.util.List;

public interface DealService {
    /**
     * This method creates Client and Application using LoanApplicationRequestDTO and returns offers
     * @param requestDTO LoanApplicationRequestDTO
     * @return 4 loan offers
     */
    List<LoanOfferDTO> application(LoanApplicationRequestDTO requestDTO);

    /**
     * This method updates application using LoanOfferDTO
     * @param loanOfferDTO LoanOfferDTO
     * @return updated application
     */
    Application updateApplication(LoanOfferDTO loanOfferDTO);

    /**
     * This method sends request to calculate credit details to Conveyor
     * @param applicationId Long applicationId
     * @param requestDTO FinishRegistrationRequestDTO
     * @return credit details as CreditDTO
     */
    CreditDTO calculateCreditByApplicationId(Long applicationId, FinishRegistrationRequestDTO requestDTO);
}
