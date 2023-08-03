package com.neoflex.conveyor.controller;

import com.neoflex.conveyor.dto.api.response.CreditDTO;
import com.neoflex.conveyor.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.api.response.LoanOfferDTO;
import com.neoflex.conveyor.dto.api.request.ScoringDataDTO;
import com.neoflex.conveyor.service.CreditService;
import com.neoflex.conveyor.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Conveyor Controller")
public class ConveyorController {
    private final LoanService loanService;

    private final CreditService creditService;

    @Operation(summary = "Получение списка возможных займов (Prescoring)")
    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("CONTROLLER: getLoanOffers");
        log.debug("Request details: {}", requestDTO);
        List<LoanOfferDTO> offers = loanService.getLoanOffers(requestDTO);
        log.debug("Response offers details: {}", offers);
        return offers;
    }

    @Operation(summary = "Вычисление данных кредита (Scoring)")
    @PostMapping("/calculation")
    public CreditDTO getCalculation(@Valid @RequestBody ScoringDataDTO dataDTO) {
        log.info("CONTROLLER: getCalculation");
        log.debug("Request details {}", dataDTO);
        CreditDTO creditDTO = creditService.calculateCreditDetails(dataDTO);
        log.debug("Response credit details: {}", creditDTO);
        return creditDTO;
    }

}
