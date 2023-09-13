package com.neoflex.deal.service.impl;

import com.neoflex.deal.exception.DealException;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final ApplicationRepository applicationRepository;
    @Override
    public Application getApplicationById(Long id) {
        Optional<Application> optApplication = applicationRepository.findById(id);
        return optApplication.orElseThrow(() -> new DealException("The application does not exist"));
    }

    @Override
    public List<Application> getAllApplications() {
        List<Application> applications = applicationRepository.findAll();

        if (applications == null || applications.isEmpty()) {
            return Collections.emptyList();
        }

        return applications;
    }
}
