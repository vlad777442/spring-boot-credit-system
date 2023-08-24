package com.neoflex.deal.mapper;

import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ScoringDataMapper {
    public ScoringDataDTO mapToScoringDataDTO(Application application, Client client, FinishRegistrationRequestDTO requestDTO) {
        return ScoringDataDTO.builder()
                .amount(application.getAppliedOffer().getRequestedAmount())
                .term(application.getAppliedOffer().getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(requestDTO.getGender())
                .birthdate(client.getBirthDate())
                .passportSeries(client.getPassport().getSeries())
                .passportNumber(client.getPassport().getNumber())
                .passportIssueDate(requestDTO.getPassportIssueDate())
                .passportIssueBranch(requestDTO.getPassportIssueBrach())
                .maritalStatus(requestDTO.getMaritalStatus())
                .dependentAmount(requestDTO.getDependentAmount())
                .employment(requestDTO.getEmployment())
                .account(requestDTO.getAccount())
                .isInsuranceEnabled(application.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(application.getAppliedOffer().getIsSalaryClient())
                .build();
    }
}
