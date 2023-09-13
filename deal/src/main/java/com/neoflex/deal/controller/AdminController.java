package com.neoflex.deal.controller;

import com.neoflex.deal.model.Application;
import com.neoflex.deal.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deal/admin")
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/application/{applicationId}")
    public Application getApplication(@RequestParam Long applicationId) {
        log.info("Getting application with id {}", applicationId);
        return adminService.getApplicationById(applicationId);
    }

    @GetMapping("/application")
    public List<Application> getAllApplications() {
        log.info("Getting all applications");
        return adminService.getAllApplications();
    }
}
