package com.neoflex.deal.mapper;

import com.neoflex.deal.dto.api.request.EmploymentDTO;
import com.neoflex.deal.model.Employment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmploymentMapper {
    Employment mapEmployment(EmploymentDTO employmentDTO);
}
