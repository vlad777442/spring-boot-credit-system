package com.neoflex.gateway.controller;

import com.neoflex.gateway.client.ApplicationClient;
import com.neoflex.gateway.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.gateway.dto.api.response.LoanOfferDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Application Controller")
public class ApplicationController {
    private final ApplicationClient applicationClient;

    @Operation(summary = "Получение списка 4 возможных займов")
    @PostMapping("/")
    public List<LoanOfferDTO> getOffers(@RequestBody @Valid LoanApplicationRequestDTO requestDTO) {
        log.info("Requesting loan offers from MC Application");
        return applicationClient.getOffers(requestDTO);
    }

    @Operation(summary = "Выбор одного из предложений")
    @PutMapping("/offer")
    public void applyApplication(@RequestBody @Valid LoanOfferDTO loanOfferDTO) {
        log.info("Sending apply loan offer to MC Application");
        applicationClient.applyApplication(loanOfferDTO);
    }
}
