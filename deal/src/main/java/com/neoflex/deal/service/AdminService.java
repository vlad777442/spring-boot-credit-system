package com.neoflex.deal.service;

import com.neoflex.deal.model.Application;

import java.util.List;

public interface AdminService {
    Application getApplicationById(Long id);

    List<Application> getAllApplications();
}
