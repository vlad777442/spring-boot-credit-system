package com.neoflex.deal.mapper.impl;

import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.mapper.LoanOfferMapper;
import com.neoflex.deal.model.LoanOffer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Primary
@Component
public class LoanOfferMapperMyImpl implements LoanOfferMapper {
    @Override
    public LoanOffer mapToLoanOffer(LoanOfferDTO loanOfferDTO) {
        if ( loanOfferDTO == null ) {
            return null;
        }

        LoanOffer.LoanOfferBuilder loanOffer = LoanOffer.builder();

        return loanOffer.build();
    }

    @Override
    public LoanOfferDTO mapToLoanOfferDTO(LoanOffer loanOffer) {
        if ( loanOffer == null ) {
            return null;
        }

        return LoanOfferDTO.builder()
                .applicationId(loanOffer.getApplicationId())
                .requestedAmount(loanOffer.getRequestedAmount())
                .totalAmount(loanOffer.getTotalAmount())
                .term(loanOffer.getTerm())
                .monthlyPayment(loanOffer.getMonthlyPayment())
                .rate(loanOffer.getRate())
                .isInsuranceEnabled(loanOffer.getIsInsuranceEnabled())
                .isSalaryClient(loanOffer.getIsSalaryClient())
                .build();
    }

    @Override
    public List<LoanOfferDTO> mapToListLoanOfferDTO(List<LoanOffer> loanOffers) {
        if ( loanOffers == null ) {
            return Collections.emptyList();
        }

        return loanOffers.stream()
                .map(this::mapToLoanOfferDTO)
                .toList();
    }
}
