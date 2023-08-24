package com.neoflex.deal.mapper;

import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.model.Credit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit mapCredit(CreditDTO creditDTO);
}
