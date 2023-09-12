package com.neoflex.dossier.service;

public interface EmailService {
    String sendEmail(String to, String subject, String body);

    String sendEmailWithAttachments(String to, String subject, String body, String[] filesPath);
}
