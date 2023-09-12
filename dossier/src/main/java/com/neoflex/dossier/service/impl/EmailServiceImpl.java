package com.neoflex.dossier.service.impl;

import com.neoflex.dossier.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

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
            log.error("Error occured {}", e.getMessage());
            return "Error while Sending Mail";
        }
    }

    @Override
    public String sendEmailWithAttachments(String to, String subject, String body, String[] filesPath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setFrom("${spring.mail.username}");
            helper.setSubject(subject);
            helper.setText(body);

            for (String attachmentPath: filesPath) {
                if (attachmentPath != null && !attachmentPath.isEmpty()) {
                    File attachment = new File(attachmentPath);
                    if (attachment.exists()) {
                        helper.addAttachment(attachment.getName(), attachment);
                    } else {
                        log.warn("Attachment file not found at: {}", attachmentPath);
                    }
                }
            }
            mailSender.send(message);
            log.info("Message with attachment sent to {}", to);

            return "Mail Sent Successfully...";
        } catch (MessagingException e) {
            log.error("Error occurred: {}", e.getMessage());
            return "Error while Sending Mail";
        }
    }
}
