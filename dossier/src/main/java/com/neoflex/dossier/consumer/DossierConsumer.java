package com.neoflex.dossier.consumer;

import com.neoflex.dossier.dto.EmailMessageDTO;
import com.neoflex.dossier.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DossierConsumer {
    private final EmailService emailService;
    @KafkaListener(topics = {
            "finish-registration",
            "create-documents",
            "send-documents",
            "send-ses",
            "credit-issued",
            "application-denied"},
            groupId = "deal")
    public void listener(EmailMessageDTO emailMessageDTO) {
        log.info("Request for project status change received: " + emailMessageDTO.toString());

        emailService.sendEmail(emailMessageDTO.getAddress(), emailMessageDTO.getTheme().toString(),
                emailMessageDTO.getApplicationId().toString());
        log.info("Sent email request to EmailService");
    }
}
