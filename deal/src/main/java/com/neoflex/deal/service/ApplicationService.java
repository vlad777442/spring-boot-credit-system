package com.neoflex.deal.service;

import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.model.Application;
import com.neoflex.deal.model.Client;

public interface ApplicationService {
    Client createClient(LoanApplicationRequestDTO requestDTO);

    Application createApplication(Client client);
}
