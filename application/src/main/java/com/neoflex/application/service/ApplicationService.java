package com.neoflex.application.service;

import com.neoflex.application.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.application.dto.api.response.LoanOfferDTO;

import java.util.List;

public interface ApplicationService {
    List<LoanOfferDTO> application(LoanApplicationRequestDTO request);

    void applyLoanOffer(LoanOfferDTO loanOffer);
}
