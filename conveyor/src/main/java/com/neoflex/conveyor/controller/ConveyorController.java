package com.neoflex.conveyor.controller;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.service.CreditService;
import com.neoflex.conveyor.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Log4j2
@Tag(name = "Conveyor Controller")
public class ConveyorController {
    @Autowired
    LoanService loanService;

    @Autowired
    CreditService creditService;

    @Operation(summary = "Получение списка возможных займов (Prescoring)")
    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(@Valid @RequestBody LoanApplicationRequestDTO requestDTO) {
        log.info("CONTROLLER: getLoanOffers");
        List<LoanOfferDTO> offers = loanService.getLoanOffers(requestDTO);
        return offers;
    }

    @Operation(summary = "Вычисление данных кредита (Scoring)")
    @PostMapping("/calculation")
    public CreditDTO getCalculation(@Valid @RequestBody ScoringDataDTO dataDTO) {
        log.info("CONTROLLER: getCalculation");
        CreditDTO creditDTO = creditService.calculateCreditDetails(dataDTO);
        return creditDTO;
    }

}
