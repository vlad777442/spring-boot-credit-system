package com.neoflex.application.service.impl;

import com.neoflex.application.client.DealClient;
import com.neoflex.application.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.application.dto.api.response.LoanOfferDTO;
import com.neoflex.application.exception.ApplicationException;
import com.neoflex.application.service.ApplicationService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    private final DealClient dealClient;

    @Override
    public List<LoanOfferDTO> application(LoanApplicationRequestDTO requestDTO) {
        log.info("Requesting loan offers from MC Deal");
        log.debug("LoanApplicationRequestDTO {}", requestDTO);

        try {
            List<LoanOfferDTO> loanOffers = dealClient.application(requestDTO);
            log.info("Received {} loan offers from MC Deal", loanOffers.size());
            return loanOffers;
        } catch (FeignException ex) {
            log.error("Feign Client error during loan offer request: {}", ex.getMessage(), ex);
            throw new ApplicationException(ex.getMessage());
        }
    }

    @Override
    public void applyLoanOffer(LoanOfferDTO loanOffer) {
        log.info("Applying loan offer using MC Deal {}", loanOffer);
        log.debug("LoanOfferDTO {}", loanOffer);

        try {
            dealClient.applyLoanOffer(loanOffer);
            log.info("Finished applying offer");
        } catch (FeignException ex) {
            log.error("Feign Client error during applying loan offer: {}", ex.getMessage(), ex);
            throw new ApplicationException(ex.getMessage());
        }
    }
}
