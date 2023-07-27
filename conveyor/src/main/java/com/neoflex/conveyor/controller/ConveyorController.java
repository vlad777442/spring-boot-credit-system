package com.neoflex.conveyor.controller;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conveyor")
public class ConveyorController {
    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDTO>> getOffers(LoanApplicationRequestDTO requestDTO) {
        List<LoanOfferDTO> offers = null;
        return null;
    }

    @PostMapping("/calculation ")
    public ResponseEntity<CreditDTO> getCalculation(ScoringDataDTO dataDTO) {
        CreditDTO creditDTO = new CreditDTO();
        return null;
    }

}
