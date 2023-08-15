package com.neoflex.deal.service.mapper;

import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.model.Credit;
import org.mapstruct.Mapper;

@Mapper
public interface CreditMapper {
    Credit mapCredit(CreditDTO creditDTO);
}
