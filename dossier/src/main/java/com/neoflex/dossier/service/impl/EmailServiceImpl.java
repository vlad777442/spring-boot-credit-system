package com.neoflex.dossier.service.impl;

import com.neoflex.dossier.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Override
    public final String sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom("${spring.mail.username}");
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Message sent to {}", to);

            return "Mail Sent Successfully...";
        } catch (Exception e) {
            log.error("Error occured {}", e);
            return "Error while Sending Mail";
        }

    }
}
