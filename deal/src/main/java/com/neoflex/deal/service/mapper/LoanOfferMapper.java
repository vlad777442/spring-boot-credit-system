package com.neoflex.deal.service.mapper;

import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.model.LoanOffer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanOfferMapper {
    LoanOffer mapLoanOffer(LoanOfferDTO loanOfferDTO);
    LoanOfferDTO mapLoanOfferDTO(LoanOffer loanOffer);
    List<LoanOfferDTO> mapListLoanOfferDTO(List<LoanOffer> loanOffer);
}
