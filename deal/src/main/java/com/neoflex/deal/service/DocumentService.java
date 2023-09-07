package com.neoflex.deal.service;

public interface DocumentService {
    void sendDocument(Long applicationId);

    void requestDocumentSigning(Long applicationId);

    void signDocumentByCode(Long applicationId);
}
