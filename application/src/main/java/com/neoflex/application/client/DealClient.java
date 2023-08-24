package com.neoflex.application.client;

import com.neoflex.application.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.application.dto.api.response.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "conveyor", url = "${service.integration.deal.url}")
public interface DealClient {
    @PostMapping("/application")
    List<LoanOfferDTO> application(@RequestBody LoanApplicationRequestDTO request);

    @PutMapping("/offer")
    void applyLoanOffer(@RequestBody LoanOfferDTO loanOffer);
}
