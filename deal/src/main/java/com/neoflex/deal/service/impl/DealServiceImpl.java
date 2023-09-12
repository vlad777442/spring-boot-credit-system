package com.neoflex.deal.service.impl;

import com.neoflex.deal.client.ConveyorClient;
import com.neoflex.deal.dto.api.request.EmploymentDTO;
import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.dto.enums.EmailThemeType;
import com.neoflex.deal.exception.DealException;
import com.neoflex.deal.mapper.CreditMapper;
import com.neoflex.deal.mapper.EmploymentMapper;
import com.neoflex.deal.mapper.LoanOfferMapper;
import com.neoflex.deal.mapper.ScoringDataMapper;
import com.neoflex.deal.model.*;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.ChangeType;
import com.neoflex.deal.model.enums.CreditStatus;
import com.neoflex.deal.producer.DocumentProducer;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import com.neoflex.deal.repository.CreditRepository;
import com.neoflex.deal.service.DealService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealServiceImpl implements DealService {
    private final ApplicationRepository applicationRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final ConveyorClient conveyorClient;
    private final EmploymentMapper employmentMapper;
    private final CreditMapper creditMapper;
    private final LoanOfferMapper loanOfferMapper;
    private final ScoringDataMapper scoringDataMapper;
    private final DocumentProducer documentProducer;

    @Value("${credit.minRate}")
    private BigDecimal minRate;

    @Value("${credit.maxRate}")
    private BigDecimal maxRate;

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
    public List<LoanOfferDTO> application(LoanApplicationRequestDTO requestDTO) {
        log.info("Getting loan offers");
        log.debug("Getting offers from request {}", requestDTO);

        Client client =  clientRepository.save(createClient(requestDTO));
        Application application = createApplication(client);
        Application savedApplication = applicationRepository.save(application);

        List<LoanOffer> offers = retrieveLoanOffersFromConveyor(requestDTO);
        offers.forEach(offer -> offer.setApplicationId(savedApplication.getApplicationId()));

        return loanOfferMapper.mapToListLoanOfferDTO(offers);
    }

    private List<LoanOffer> retrieveLoanOffersFromConveyor(LoanApplicationRequestDTO requestDTO) {
        try {
            return conveyorClient.getLoanOffers(requestDTO);
        } catch (FeignException ex) {
            log.error("Error while retrieving loan offers from conveyor: {}", ex.getMessage());
            throw new DealException(ex.getMessage());
        }
    }

    @Override
    public Application updateApplication(LoanOfferDTO loanOffer) {
        log.info("UPDATING APPLICATION");
        log.debug("Updating application from loan offer {}", loanOffer);

        Optional<Application> optApplication = applicationRepository.findById(loanOffer.getApplicationId());
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        application.setAppliedOffer(loanOfferMapper.mapToLoanOffer(loanOffer));
        application = updateApplicationStatusAndHistory(ApplicationStatus.APPROVED, application);

        applicationRepository.save(application);
        log.info("Application updated");

        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.FINISH_REGISTRATION));

        return application;
    }

    public StatusHistory buildApplicationHistory(ApplicationStatus status, ChangeType type) {
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

        ScoringDataDTO scoringDataDTO = scoringDataMapper.mapToScoringDataDTO(application, client, requestDTO);
        CreditDTO creditDTO = conveyorClient.getCalculation(scoringDataDTO);
        updateCreditByCreditDTO(creditDTO, application);
        denialCheck(application);
        log.info("Credit calculated");

        return creditDTO;
    }

    private void updateClientByEmployment(EmploymentDTO employmentDTO, Client client) {
        log.info("Updating client by employmentDTO");
        log.debug("Request {} {}", employmentDTO, client);

        Employment employment = employmentMapper.mapEmployment(employmentDTO);
        client.setEmployment(employment);
        clientRepository.save(client);

        log.info("Client updated");
    }

    private void updateCreditByCreditDTO(CreditDTO creditDTO, Application application) {
        log.info("Updating credit from CreditDTO");
        log.debug("Updating credit by CreditDTO {} {}", creditDTO, application);

        Credit credit = creditMapper.mapCredit(creditDTO);
        credit.setCreditStatus(CreditStatus.CALCULATED);
        application.setCredit(credit);
        creditRepository.save(credit);
        applicationRepository.save(application);

        log.info("Credit updated");
    }

    private void denialCheck(Application application) {
        boolean isGreaterThan = application.getCredit().getRate().compareTo(maxRate) > 0;
        boolean isLessThan = application.getCredit().getRate().compareTo(minRate) < 0;
        ApplicationStatus status;

        if (isGreaterThan || isLessThan) {
            status = ApplicationStatus.CC_DENIED;
            documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.APPLICATION_DENIED));
        } else {
            status = ApplicationStatus.CC_APPROVED;
            documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.CREATE_DOCUMENTS));
        }
        applicationRepository.save(updateApplicationStatusAndHistory(status, application));
    }

    public Application updateApplicationStatusAndHistory(ApplicationStatus status, Application application) {
        application.setStatus(status);
        updateApplicationStatusHistory(status, application);

        return application;
    }

    private void updateApplicationStatusHistory(ApplicationStatus status, Application application) {
        List<StatusHistory> histories = application.getStatusHistory();
        histories.add(buildApplicationHistory(status, ChangeType.AUTOMATIC));
        application.setStatusHistory(histories);
    }
}
