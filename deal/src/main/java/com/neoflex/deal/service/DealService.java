package com.neoflex.deal.service;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.Application;

import java.util.List;

public interface DealService {
    List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO);

    Application updateApplication(LoanOfferDTO loanOfferDTO);

    CreditDTO calculateCreditByApplicationId(Long applicationId, FinishRegistrationRequestDTO requestDTO);
}
