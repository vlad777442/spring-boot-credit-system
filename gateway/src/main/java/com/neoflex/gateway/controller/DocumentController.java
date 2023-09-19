package com.neoflex.gateway.controller;

import com.neoflex.gateway.client.DealClient;
import com.neoflex.gateway.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.gateway.dto.api.response.CreditDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Deal Document Controller")
public class DocumentController {
    private final DealClient dealClient;

    @Operation(summary = "Завершение регистрации и расчет условий кредита")
    @PostMapping("application/registration/{applicationId}")
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

    @Operation(summary = "Подписание документов с помощью кода")
    @PostMapping("/{applicationId}/code")
    public void signDocumentByCode(@PathVariable Long applicationId, @RequestBody Integer sesCode) {
        log.info("Sending request to sign document by code {} to MC Deal", applicationId);
        dealClient.signDocumentByCode(applicationId, sesCode);
    }
}
