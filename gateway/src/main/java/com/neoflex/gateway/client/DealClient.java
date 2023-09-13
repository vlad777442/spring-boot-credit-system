package com.neoflex.gateway.client;

import com.neoflex.gateway.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.gateway.dto.api.response.CreditDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(value = "deal", url = "${service.integration.deal.url}")
public interface DealClient {
    @PutMapping("/calculate/{applicationId}")
    CreditDTO calculateCreditByApplicationId(@PathVariable Long applicationId,
                                             @RequestBody FinishRegistrationRequestDTO requestDTO);

    @PostMapping("/document/{applicationId}/send")
    void requestSendDocument(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/sign")
    void requestDocumentSigning(@PathVariable Long applicationId);

    @PostMapping("/document/{applicationId}/code")
    public void signDocumentByCode(@PathVariable Long applicationId, @RequestBody Integer sesCode);
}
