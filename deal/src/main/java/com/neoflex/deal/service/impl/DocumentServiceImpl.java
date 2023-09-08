package com.neoflex.deal.service.impl;

import com.neoflex.deal.dto.enums.EmailThemeType;
import com.neoflex.deal.exception.DealException;
import com.neoflex.deal.exception.SesException;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.Credit;
import com.neoflex.deal.model.StatusHistory;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.ChangeType;
import com.neoflex.deal.model.enums.CreditStatus;
import com.neoflex.deal.producer.DocumentProducer;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.CreditRepository;
import com.neoflex.deal.service.DealService;
import com.neoflex.deal.service.DocumentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final ApplicationRepository applicationRepository;

    private final CreditRepository creditRepository;

    private final DocumentProducer documentProducer;

    private final DealService dealService;

    @Override
    public void sendDocument(Long applicationId) {
        log.info("Received SEND_DOCUMENTS. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        application.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
        applicationRepository.save(application);

        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.SEND_DOCUMENTS));
        log.info("Document has been successfully sent to DocumentProducer");
    }

    @Override
    @Transactional
    public void requestDocumentSigning(Long applicationId) {
        log.info("Received SEND_SES. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        String sesCode = generateSesCode();
        application.setSesCode(sesCode);
        log.info("Ses code {} for application #{} was generated", sesCode, applicationId);
        application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        applicationRepository.save(application);

        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.SEND_SES));
        log.info("Document has been successfully sent to DocumentProducer");
    }

    @Override
    public void signDocumentByCode(Long applicationId, Integer sesCode) {
        log.info("Received DOCUMENT_SIGNED. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));
        List<StatusHistory> statusHistoryList = application.getStatusHistory();

        if (checkSesCode(application, sesCode)) {
            application = updateCredit(application, CreditStatus.ISSUED);
            application.setSesCode(sesCode.toString());
            application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
            application.setSignDate(LocalDateTime.now());
            statusHistoryList.add(dealService.buildApplicationHistory(ApplicationStatus.DOCUMENT_SIGNED, ChangeType.AUTOMATIC));
            applicationRepository.save(application);

            documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.CREDIT_ISSUED));
            log.info("Document has been successfully sent to DocumentProducer");
        } else {
            log.error("Ses code is incorrect");
            throw new SesException("Ses number is incorrect");
        }
    }

    private Application updateCredit(Application application, CreditStatus creditStatus) {
        log.info("Updating credit details");

        if (application.getCredit() == null) {
            throw new DealException("The credit is null");
        }

        Credit credit = application.getCredit();
        credit.setCreditStatus(creditStatus);
        application.setCredit(creditRepository.save(credit));

        log.info("Credit successfully updated");
        return applicationRepository.save(application);
    }

    private String generateSesCode() {
        Random random = new Random();
        int min = 1000;
        int max = 10000;

        return String.valueOf(random.nextInt((max - min) + 1) + min);
    }

    private boolean checkSesCode(Application application, Integer sesCode) {
        if (application.getSesCode().equals(sesCode.toString()))
            return true;
        return false;
    }
}
