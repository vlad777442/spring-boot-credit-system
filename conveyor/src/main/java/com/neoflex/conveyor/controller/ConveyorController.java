package com.neoflex.conveyor.controller;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.service.CreditService;
import com.neoflex.conveyor.service.LoanService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
@Log4j2
public class ConveyorController {
    @Autowired
    LoanService loanService;

    @Autowired
    CreditService creditService;

    @PostMapping("/offers")
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO) {
        log.info("CONTROLLER: getLoanOffers");
        List<LoanOfferDTO> offers = loanService.getLoanOffers(requestDTO);
        return offers;
    }

    @PostMapping("/calculation")
    public CreditDTO getCalculation(ScoringDataDTO dataDTO) {
        log.info("CONTROLLER: getCalculation");
        CreditDTO creditDTO = creditService.calculateCreditDetails(dataDTO);
        return creditDTO;
    }

}
