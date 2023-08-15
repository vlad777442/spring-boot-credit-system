package com.neoflex.deal.controller;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.service.ApplicationService;
import com.neoflex.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@EnableFeignClients
public class DealController {
    private DealService dealService;
    private ApplicationService applicationService;

    @PostMapping("/application")
    public List<LoanOfferDTO> getOffers(@RequestBody LoanApplicationRequestDTO requestDTO) {
        return dealService.getLoanOffers(requestDTO);
    }

    @PutMapping("/offer")
    public Application updateApplication(@RequestBody LoanOfferDTO loanOfferDTO) {
        return dealService.updateApplication(loanOfferDTO);
    }

    @PutMapping("/calculate/{applicationId}")
    public CreditDTO calculateCreditByApplicationId(@PathVariable Long applicationId, @RequestBody FinishRegistrationRequestDTO requestDTO) {
        return dealService.calculateCreditByApplicationId(applicationId, requestDTO);
    }

}
