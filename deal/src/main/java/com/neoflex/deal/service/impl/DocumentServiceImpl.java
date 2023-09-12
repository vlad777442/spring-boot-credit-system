package com.neoflex.deal.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.deal.dto.enums.EmailThemeType;
import com.neoflex.deal.exception.DealException;
import com.neoflex.deal.exception.SesException;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.Credit;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.CreditStatus;
import com.neoflex.deal.producer.DocumentProducer;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.CreditRepository;
import com.neoflex.deal.service.DealService;
import com.neoflex.deal.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final ApplicationRepository applicationRepository;

    private final CreditRepository creditRepository;

    private final DocumentProducer documentProducer;

    private final DealService dealService;

    private final ObjectMapper objectMapper;

    @Value("${deal.dataPath}")
    private String absoluteDirectory;

    @Override
    public void sendDocument(Long applicationId) {
        log.info("Received SEND_DOCUMENTS. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        applicationRepository.save(dealService.updateApplicationStatusAndHistory(ApplicationStatus.PREPARE_DOCUMENTS, application));

        saveToFile(application.getApplicationId(), "credit", application.getCredit());
        saveToFile(application.getApplicationId(), "client", application.getClient());
        saveToFile(application.getApplicationId(), "paymentSchedule", application.getCredit().getPaymentSchedule());
        applicationRepository.save(dealService.updateApplicationStatusAndHistory(ApplicationStatus.DOCUMENT_CREATED, application));

        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.SEND_DOCUMENTS));
        log.info("Document has been successfully sent to DocumentProducer");
    }

    @Override
    public void requestDocumentSigning(Long applicationId) {
        log.info("Received SEND_SES. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));
        applicationRepository.save(generateSesCode(application));

        saveToFile(application.getApplicationId(), "ses", application.getSesCode());
        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.SEND_SES));
        log.info("Document has been successfully sent to DocumentProducer");
    }

    @Override
    public void signDocumentByCode(Long applicationId, Integer sesCode) {
        log.info("Received DOCUMENT_SIGNED. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));

        if (checkSesCode(application, sesCode)) {
            application = updateCredit(application, CreditStatus.ISSUED);
            application.setSesCode(sesCode.toString());
            application.setSignDate(LocalDateTime.now());
            applicationRepository.save(dealService.updateApplicationStatusAndHistory(ApplicationStatus.DOCUMENT_SIGNED, application));

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

    private Application generateSesCode(Application application) {
        log.info("Generating SES code");
        Random random = new Random();
        int min = 1000;
        int max = 10000;
        application.setSesCode(String.valueOf(random.nextInt((max - min) + 1) + min));

        log.info("SES code was generated");
        return application;
    }

    private boolean checkSesCode(Application application, Integer sesCode) {
        return application.getSesCode().equals(sesCode.toString());
    }

    private File saveToFile(Long applicationId, String type, Object data) {
        log.info("Saving to file");

        String directoryPath = absoluteDirectory + "/" + applicationId + "/";
        String fileName = type + ".txt";
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            String dataJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
            Files.write(file.toPath(), dataJson.getBytes());
            log.info("Data saved to file: {}", file.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error converting data to JSON or writing to file: {}", e.getMessage(), e);
        }

        return file;
    }
}
