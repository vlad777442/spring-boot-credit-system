package com.neoflex.deal.service.impl;

import com.neoflex.deal.dto.enums.EmailThemeType;
import com.neoflex.deal.exception.DealException;
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

import java.util.List;
import java.util.Optional;

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

        application.setSesCode(Long.toHexString(applicationId));
        application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        applicationRepository.save(application);

        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.SEND_SES));
        log.info("Document has been successfully sent to DocumentProducer");
    }

    @Override
    public void signDocumentByCode(Long applicationId) {
        log.info("Received DOCUMENT_SIGNED. Sending request to DocumentProducer");

        Optional<Application> optApplication = applicationRepository.findById(applicationId);
        Application application = optApplication.orElseThrow(() -> new DealException("The application does not exist"));
        List<StatusHistory> statusHistoryList = application.getStatusHistory();

        application = updateCredit(application, CreditStatus.ISSUED);
        application.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        statusHistoryList.add(dealService.buildApplicationHistory(ApplicationStatus.DOCUMENT_SIGNED, ChangeType.AUTOMATIC));
        applicationRepository.save(application);

        documentProducer.send(documentProducer.createEmailMessageDTO(application, EmailThemeType.CREDIT_ISSUED));
        log.info("Document has been successfully sent to DocumentProducer");
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
}
