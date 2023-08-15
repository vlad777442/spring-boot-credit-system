package com.neoflex.deal.service;

import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "conveyor", url = "http://localhost:8080/conveyor")
public interface ConveyorClient {
    @PostMapping("/offers")
    List<LoanOfferDTO> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO);

    @PostMapping("/calculation")
    public CreditDTO getCalculation(@Valid @RequestBody ScoringDataDTO dataDTO);
}
