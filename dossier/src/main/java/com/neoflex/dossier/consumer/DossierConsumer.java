package com.neoflex.dossier.consumer;

import com.neoflex.dossier.dto.EmailMessageDTO;
import com.neoflex.dossier.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierConsumer {
    private final EmailService emailService;

    @Value("${deal.dataPath}")
    private String absolutePath;

    @KafkaListener(topics = "finish-registration", groupId = "deal")
    public void listenFinishRegistration(EmailMessageDTO emailMessageDTO) {
        log.info("Request for finish registration received: " + emailMessageDTO.toString());
        String body = "Завершите оформление заявки №" + emailMessageDTO.getApplicationId() + ".";

        emailService.sendEmail(
                emailMessageDTO.getAddress(),
                emailMessageDTO.getTheme().toString(),
                body
        );

        log.info("Sent email request to EmailService for finish registration");
    }

    @KafkaListener(topics = "create-documents", groupId = "deal")
    public void listenCreateDocuments(EmailMessageDTO emailMessageDTO) {
        log.info("Request for create documents received: " + emailMessageDTO.toString());
        String body = "Подготовлены документы для заявки №" + emailMessageDTO.getApplicationId();

        emailService.sendEmail(
                emailMessageDTO.getAddress(),
                emailMessageDTO.getTheme().toString(),
                body
        );

        log.info("Sent email request to EmailService for create documents");
    }

    @KafkaListener(topics = "send-documents", groupId = "deal")
    public void listenSendDocuments(EmailMessageDTO emailMessageDTO) {
        log.info("Request for send documents received: " + emailMessageDTO.toString());
        String body = "Документы для завки №" + emailMessageDTO.getApplicationId() + " отправлены.";
        String[] path = {
                absolutePath +  emailMessageDTO.getApplicationId() + "/credit.txt",
                absolutePath +  emailMessageDTO.getApplicationId() + "/client.txt",
                absolutePath +  emailMessageDTO.getApplicationId() + "/paymentSchedule.txt",
        };

        emailService.sendEmailWithAttachments(
                emailMessageDTO.getAddress(),
                emailMessageDTO.getTheme().toString(),
                body,
                path
        );

        log.info("Sent email request to EmailService for send documents");
    }

    @KafkaListener(topics = "send-ses", groupId = "deal")
    public void listenSendSes(EmailMessageDTO emailMessageDTO) {
        log.info("Request for send ses received: " + emailMessageDTO.toString());
        String body = "Код для подписания завки №" + emailMessageDTO.getApplicationId() + " был сгенерирован.";
        String[] path = {absolutePath +  emailMessageDTO.getApplicationId() + "/ses.txt"};

        emailService.sendEmailWithAttachments(
                emailMessageDTO.getAddress(),
                emailMessageDTO.getTheme().toString(),
                body,
                path
        );

        log.info("Sent email request to EmailService for send ses");
    }

    @KafkaListener(topics = "credit-issued", groupId = "deal")
    public void listenCreditIssued(EmailMessageDTO emailMessageDTO) {
        log.info("Request for credit issued received: " + emailMessageDTO.toString());
        String body = "Заявка №" + emailMessageDTO.getApplicationId() + " была успешно выполнена!";

        emailService.sendEmail(
                emailMessageDTO.getAddress(),
                emailMessageDTO.getTheme().toString(),
                body
        );

        log.info("Sent email request to EmailService for credit issued");
    }

    @KafkaListener(topics = "application-denied", groupId = "deal")
    public void listenApplicationDenied(EmailMessageDTO emailMessageDTO) {
        log.info("Request for application denied received: " + emailMessageDTO.toString());
        String body = "Заявка №" + emailMessageDTO.getApplicationId() + " была отклонена.";

        emailService.sendEmail(
                emailMessageDTO.getAddress(),
                emailMessageDTO.getTheme().toString(),
                body
        );

        log.info("Sent email request to EmailService for application denied");
    }
}
