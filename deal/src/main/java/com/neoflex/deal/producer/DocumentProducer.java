package com.neoflex.deal.producer;

import com.neoflex.deal.dto.api.request.EmailMessageDTO;

import com.neoflex.deal.dto.enums.EmailThemeType;
import com.neoflex.deal.model.Application;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class DocumentProducer {
    private final KafkaTemplate<String, EmailMessageDTO> kafkaTemplate;

    public void send(EmailMessageDTO message) {
        CompletableFuture<SendResult<String, EmailMessageDTO>> future = kafkaTemplate.send(
                                                            message.getTheme().toString().toLowerCase().replace("_", "-"),
                                                            message);
        future.whenComplete((sendResult, exception) -> {
                if (exception == null) {
                    log.info("Email sent to MC dossier successfully {} {}" , message, sendResult.getRecordMetadata().offset());
                } else {
                    log.warn("Error occured during an attempt to send a message {}", exception.getMessage());
                }
            });
    }

    public EmailMessageDTO createEmailMessageDTO(Application application, EmailThemeType emailThemeType) {
        log.info("Creating EmailMessageDTO");

        return EmailMessageDTO.builder()
                .address(application.getClient().getEmail())
                .theme(emailThemeType)
                .applicationId(application.getApplicationId())
                .build();
    }

}
