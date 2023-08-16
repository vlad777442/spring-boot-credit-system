package com.neoflex.deal.service.impl;

import com.neoflex.deal.dto.api.request.EmploymentDTO;
import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.exception.DealException;
import com.neoflex.deal.model.*;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.ChangeType;
import com.neoflex.deal.model.enums.CreditStatus;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import com.neoflex.deal.service.ConveyorClient;
import com.neoflex.deal.service.DealService;
import com.neoflex.deal.service.mapper.CreditMapper;
import com.neoflex.deal.service.mapper.EmploymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final ConveyorClient conveyorClient;
    private final EmploymentMapper employmentMapper;
    private final CreditMapper creditMapper;

    @Override
    public Client createClient(LoanApplicationRequestDTO requestDTO) {
        log.info("Creating client");
        log.debug("creating client from loanAppDto {}", requestDTO);

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
        log.info("Creating application");
        log.debug("creating app from client {}", client);

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

    @Override
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO) {
        log.info("Getting loan offers");
        log.debug("Getting offers from request {}", requestDTO);

        Client client =  clientRepository.save(createClient(requestDTO));
        applicationRepository.save(createApplication(client));

        return conveyorClient.getLoanOffers(requestDTO);
    }

    @Override
    public Application updateApplication(LoanOfferDTO loanOfferDTO) {
        log.info("UPDATING APPLICATION");
        log.debug("Updating application from loan offer {}", loanOfferDTO);

        Optional<Application> optApplication = applicationRepository.findById(loanOfferDTO.getApplicationId());
        Application application;

        if (optApplication.isPresent()) {
            application = optApplication.get();
        }
        else {
            throw new DealException("The application does not exist");
        }

        application.setStatus(ApplicationStatus.APPROVED);
        application.setAppliedOffer(loanOfferDTO);
        updateApplicationHistory(application);
        applicationRepository.save(application);

        return application;
    }

    private void updateApplicationHistory(Application application) {
        log.info("Updating application history");
        log.debug("Updating application history {}", application);

        List<StatusHistory> historyDTO = application.getStatusHistory();

        StatusHistory statusHistory = StatusHistory.builder()
                .status(ApplicationStatus.APPROVED)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();

        historyDTO.add(statusHistory);
    }

    @Override
    public CreditDTO calculateCreditByApplicationId(Long applicationId, FinishRegistrationRequestDTO requestDTO) {
        log.info("CALCULATING CREDIT DETAILS FROM APP ID");
        log.debug("Calculating credit from request {} {}", applicationId, requestDTO);

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application;

        if (optApplication.isPresent()) {
            application = optApplication.get();
        }
        else {
            throw new DealException("The application does not exist");
        }

        Client client = application.getClient();
        updateClientByEmployment(requestDTO.getEmployment(), client);

        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
                .amount(application.getCredit().getAmount())
                .term(application.getCredit().getTerm())
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
                .isInsuranceEnabled(application.getCredit().getIsInsuranceEnabled())
                .isSalaryClient(application.getCredit().getIsSalaryClient())
                .build();

        CreditDTO creditDTO = conveyorClient.getCalculation(scoringDataDTO);
        updateCreditByCreditDTO(creditDTO, application);

        return creditDTO;
    }

    private void updateClientByEmployment(EmploymentDTO employmentDTO, Client client) {
        log.info("Updating client by employmentDTO");
        log.debug("Request {} {}", employmentDTO, client);

        Employment employment = employmentMapper.mapEmployment(employmentDTO);
        client.setEmployment(employment);
        clientRepository.save(client);
    }

    private void updateCreditByCreditDTO(CreditDTO creditDTO, Application application) {
        log.info("Updating credit from CreditDTO");
        log.debug("Updating credit by CreditDTO {} {}", creditDTO, application);

        Credit credit = creditMapper.mapCredit(creditDTO);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        application.setCredit(credit);
        applicationRepository.save(application);
    }
}
