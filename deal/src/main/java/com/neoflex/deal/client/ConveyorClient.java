package com.neoflex.deal.client;

import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.model.LoanOffer;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "conveyor", url = "${service.integration.conveyor.url}")
public interface ConveyorClient {
    @PostMapping("/offers")
    List<LoanOffer> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO);

    @PostMapping("/calculation")
    CreditDTO getCalculation(@Valid @RequestBody ScoringDataDTO dataDTO);
}
