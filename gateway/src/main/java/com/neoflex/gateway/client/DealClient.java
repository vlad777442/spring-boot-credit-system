package com.neoflex.gateway.client;

import com.neoflex.gateway.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.gateway.dto.api.response.CreditDTO;
import com.neoflex.gateway.model.Application;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    void signDocumentByCode(@PathVariable Long applicationId, @RequestBody Integer sesCode);

    @GetMapping("/admin/application/{applicationId}")
    Application getApplicationById(@RequestParam Long applicationId);

    @GetMapping("/admin/application")
    List<Application> getAllApplications();
}
