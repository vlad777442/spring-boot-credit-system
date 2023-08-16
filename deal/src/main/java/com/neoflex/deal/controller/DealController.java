package com.neoflex.deal.controller;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@EnableFeignClients
@Tag(name = "Deal Controller")
@Slf4j
public class DealController {
    private final DealService dealService;

    @Operation(summary = "Получение списка 4 возможных займов")
    @PostMapping("/application")
    public List<LoanOfferDTO> getOffers(@RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("CONTROLLER: getOffers");
        log.debug("LoanApplicationRequest details: {}", requestDTO);

        return dealService.getLoanOffers(requestDTO);
    }

    @Operation(summary = "Выбор предложения и обновление заявки на кредит")
    @PutMapping("/offer")
    public Application updateApplication(@RequestBody LoanOfferDTO loanOfferDTO) {
        log.info("CONTROLLER: update application");
        log.debug("LoanOfferRequest details: {}", loanOfferDTO);

        return dealService.updateApplication(loanOfferDTO);
    }

    @Operation(summary = "Расчет условий кредита")
    @PutMapping("/calculate/{applicationId}")
    public CreditDTO calculateCreditByApplicationId(@PathVariable Long applicationId, @RequestBody FinishRegistrationRequestDTO requestDTO) {
        log.info("CONTROLLER: calculate credit details");
        log.debug("Request details: {} {}", applicationId, requestDTO);

        return dealService.calculateCreditByApplicationId(applicationId, requestDTO);
    }

}
