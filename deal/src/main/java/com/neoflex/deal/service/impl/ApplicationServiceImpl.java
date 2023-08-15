package com.neoflex.deal.service.impl;

import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.Client;
import com.neoflex.deal.model.Passport;
import com.neoflex.deal.model.StatusHistory;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.ChangeType;
import com.neoflex.deal.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {
    @Override
    public Client createClient(LoanApplicationRequestDTO requestDTO) {
        log.debug("creating client from loanappdto {}", requestDTO);
        Passport passport = Passport.builder()
                .series(requestDTO.getPassportSeries())
                .number(requestDTO.getPassportNumber())
                .build();

        return Client.builder()
                .lastName(requestDTO.getLastName())
                .firstName(requestDTO.getFirstName())
                .middleName(requestDTO.getMiddleName())
                .birthDate(requestDTO.getBirthdate())
                .email(requestDTO.getEmail())
                .passport(passport)
                .build();
    }

    @Override
    public Application createApplication(Client client) {
        log.debug("creating app {}", client);
        ArrayList<StatusHistory> statusHistories = new ArrayList<>();

        StatusHistory statusHistory =  StatusHistory.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();

        statusHistories.add(statusHistory);

        return Application.builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(statusHistories)
                .build();
    }
}
