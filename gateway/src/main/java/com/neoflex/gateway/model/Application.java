package com.neoflex.gateway.model;

import com.neoflex.gateway.model.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private Long applicationId;

    private Client client;

    private Credit credit;

    private ApplicationStatus status;

    private LocalDateTime creationDate;

    private LoanOffer appliedOffer;

    private LocalDateTime signDate;

    private String sesCode;

    private List<StatusHistory> statusHistory;
}
