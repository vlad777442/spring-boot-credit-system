package com.neoflex.deal.service;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.StatusHistory;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.ChangeType;

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

    /**
     * This method creates StatusHistory
     * @param status
     * @param type
     * @return
     */
    StatusHistory buildApplicationHistory(ApplicationStatus status, ChangeType type);

    /**
     * Updates application status and history
     * @param status
     * @param application
     * @return
     */
    Application updateApplicationStatusAndHistory(ApplicationStatus status, Application application);
}
