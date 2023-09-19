package com.neoflex.gateway.controller;


import com.neoflex.gateway.client.DealClient;
import com.neoflex.gateway.model.Application;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Admin Controller")
public class AdminController {
    private final DealClient dealClient;

    @Operation(summary = "Получение заявки по id")
    @GetMapping("/application/{applicationId}")
    public Application getApplication(@RequestParam Long applicationId) {
        log.info("Getting application with id {}", applicationId);
        return dealClient.getApplicationById(applicationId);
    }

    @Operation(summary = "Получение всех заявок")
    @GetMapping("/application")
    public List<Application> getAllApplications() {
        log.info("Getting all applications");
        return dealClient.getAllApplications();
    }
}
