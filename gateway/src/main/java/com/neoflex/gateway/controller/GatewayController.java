package com.neoflex.gateway.controller;

import com.neoflex.gateway.client.ApplicationClient;
import com.neoflex.gateway.client.DealClient;
import com.neoflex.gateway.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.gateway.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.gateway.dto.api.response.CreditDTO;
import com.neoflex.gateway.dto.api.response.LoanOfferDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gateway")
@RequiredArgsConstructor
@Slf4j
public class GatewayController {
    private final DealClient dealClient;
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

    @Operation(summary = "Завершение регистрации и расчет условий кредита")
    @PutMapping("/calculate/{applicationId}")
    public CreditDTO calculateCreditByApplicationId(@PathVariable Long applicationId,
                                                    @RequestBody FinishRegistrationRequestDTO requestDTO) {
        log.info("Sending request to calculate credit details to MC Deal");
        return dealClient.calculateCreditByApplicationId(applicationId, requestDTO);
    }

    @Operation(summary = "Запрос на отправку документов")
    @PostMapping("/{applicationId}/send")
    public void requestSendDocument(@PathVariable Long applicationId) {
        log.info("Sending request to send document with id {} to MC Deal", applicationId);
        dealClient.requestSendDocument(applicationId);
    }

    @Operation(summary = "Запрос на подписание документов")
    @PostMapping("/{applicationId}/sign")
    public void requestDocumentSigning(@PathVariable Long applicationId) {
        log.info("Sending request to sign document with id {} to MC Deal", applicationId);
        dealClient.requestDocumentSigning(applicationId);
    }

    @Operation(summary = "Подписание документов")
    @PostMapping("/{applicationId}/code")
    public void signDocumentByCode(@PathVariable Long applicationId, @RequestBody Integer sesCode) {
        log.info("Sending request to sign document by code {} to MC Deal", applicationId);
        dealClient.signDocumentByCode(applicationId, sesCode);
    }
}
