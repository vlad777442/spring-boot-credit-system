package com.neoflex.deal.service.impl;

import com.neoflex.deal.dto.api.request.EmploymentDTO;
import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.model.LoanOffer;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final ConveyorClient conveyorClient;
    private final EmploymentMapper employmentMapper;
    private final CreditMapper creditMapper;

    private Client createClient(LoanApplicationRequestDTO requestDTO) {
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

    private Application createApplication(Client client) {
        log.info("Creating application");
        log.debug("creating app from client {}", client);

        List<StatusHistory> statusHistories = List.of(StatusHistory.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build());

        return Application.builder()
                .client(client)
                .creationDate(LocalDateTime.now())
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(statusHistories)
                .build();
    }

    @Override
    @Transactional
    public List<LoanOffer> application(LoanApplicationRequestDTO requestDTO) {
        log.info("Getting loan offers");
        log.debug("Getting offers from request {}", requestDTO);

        Client client =  clientRepository.save(createClient(requestDTO));
        Application application = createApplication(client);
        applicationRepository.save(application);

        List<LoanOffer> offers = conveyorClient.getLoanOffers(requestDTO);
        AtomicLong counter = new AtomicLong(1L);
        offers.forEach(offer -> offer.setApplicationId(counter.getAndIncrement()));

        return offers;
    }

    @Override
    public Application updateApplication(LoanOffer loanOffer) {
        log.info("UPDATING APPLICATION");
        log.debug("Updating application from loan offer {}", loanOffer);

        Optional<Application> optApplication = applicationRepository.findById(loanOffer.getApplicationId());
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        application.setStatus(ApplicationStatus.APPROVED);
        application.setAppliedOffer(loanOffer);

        List<StatusHistory> histories = application.getStatusHistory();
        histories.add(updateApplicationHistory(ApplicationStatus.APPROVED, ChangeType.AUTOMATIC));
        application.setStatusHistory(histories);

        applicationRepository.save(application);

        return application;
    }

    private StatusHistory updateApplicationHistory(ApplicationStatus status, ChangeType type) {
        log.info("Updating application history");
        log.debug("Updating application history {} {}", status, type);

        return StatusHistory.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(type)
                .build();
    }

    @Override
    @Transactional
    public CreditDTO calculateCreditByApplicationId(Long applicationId, FinishRegistrationRequestDTO requestDTO) {
        log.info("CALCULATING CREDIT DETAILS FROM APP ID");
        log.debug("Calculating credit from request {} {}", applicationId, requestDTO);

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        Client client = application.getClient();
        updateClientByEmployment(requestDTO.getEmployment(), client);

        ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
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
