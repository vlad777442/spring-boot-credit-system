package com.neoflex.deal.controller;

import com.neoflex.deal.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/deal/document")
@Tag(name = "Deal Document Controller")
public class DocumentController {
    private final DocumentService documentService;
    @PostMapping("/{applicationId}/send")
    public void requestSendDocument(@PathVariable Long applicationId) {
        log.info("Sending request to email document with id {}", applicationId);
        documentService.sendDocument(applicationId);
    }

    @PostMapping("/{applicationId}/sign")
    public void requestDocumentSigning(@PathVariable Long applicationId) {
        log.info("Sending request to sign document with id {}", applicationId);
        documentService.requestDocumentSigning(applicationId);
    }

    @PostMapping("/{applicationId}/code")
    public void signDocumentByCode(@PathVariable Long applicationId, @RequestBody Integer sesCode) {
        log.info("Sending request to sign document by code {}", applicationId);
        documentService.signDocumentByCode(applicationId, sesCode);
    }
}
