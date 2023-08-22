package com.neoflex.deal.service;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.LoanOffer;

import java.util.List;

public interface DealService {
    List<LoanOffer> application(LoanApplicationRequestDTO requestDTO);

    Application updateApplication(LoanOffer loanOffer);

    CreditDTO calculateCreditByApplicationId(Long applicationId, FinishRegistrationRequestDTO requestDTO);
}
