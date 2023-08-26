package com.neoflex.application.controller;

import com.neoflex.application.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.application.dto.api.response.LoanOfferDTO;
import com.neoflex.application.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
@Tag(name = "Application controller")
public class ApplicationController {
    private final ApplicationService applicationService;
    @Operation(summary = "Получение списка 4 возможных займов")
    @PostMapping("/")
    public List<LoanOfferDTO> getOffers(@RequestBody @Valid LoanApplicationRequestDTO requestDTO) {

        return applicationService.application(requestDTO);
    }

    @Operation(summary = "Выбор одного из предложений")
    @PutMapping("/offer")
    public void applyApplication(@RequestBody @Valid LoanOfferDTO loanOfferDTO) {
        applicationService.applyLoanOffer(loanOfferDTO);
    }
}
