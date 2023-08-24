package com.neoflex.deal.mapper;

import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.LoanOffer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {
    LoanOffer mapToLoanOffer(LoanOfferDTO loanOfferDTO);
    LoanOfferDTO mapToLoanOfferDTO(LoanOffer loanOffer);
    List<LoanOfferDTO> mapToListLoanOfferDTO(List<LoanOffer> loanOffer);
}
