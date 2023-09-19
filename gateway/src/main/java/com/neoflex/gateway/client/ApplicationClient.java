package com.neoflex.gateway.client;

import com.neoflex.gateway.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.gateway.dto.api.response.LoanOfferDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Component
@FeignClient(value = "application", url = "${service.integration.application.url}")
public interface ApplicationClient {
    @PostMapping("/")
    List<LoanOfferDTO> getOffers(@RequestBody @Valid LoanApplicationRequestDTO requestDTO);

    @PutMapping("/offer")
    void applyApplication(@RequestBody @Valid LoanOfferDTO loanOfferDTO);
}
