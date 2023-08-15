package com.neoflex.deal.service.mapper;

import com.neoflex.deal.dto.api.request.EmploymentDTO;
import com.neoflex.deal.model.Employment;
import org.mapstruct.Mapper;

@Mapper
public interface EmploymentMapper {
    Employment mapEmployment(EmploymentDTO employmentDTO);
}
