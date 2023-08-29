package com.neoflex.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neoflex.application.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.application.dto.api.response.LoanOfferDTO;
import com.neoflex.application.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {
    @MockBean
    private ApplicationService applicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private LoanApplicationRequestDTO getLoanApplicationRequestDTO() {
        return LoanApplicationRequestDTO.builder()
                .amount(BigDecimal.valueOf(100000))
                .term(12)
                .firstName("Sarah")
                .lastName("Williams")
                .middleName("Alison")
                .email("sarah@example.com")
                .birthdate(LocalDate.of(1995, 10, 15))
                .passportSeries("1234")
                .passportNumber("123456")
                .build();
    }

    private LoanOfferDTO getLoanOfferDTO() {
        return LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(115000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9583.33))
                .rate(BigDecimal.valueOf(15.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
    }

    @Test
    void getOffers() throws Exception {
        LoanOfferDTO loanOffer1 = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(115000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9583.33))
                .rate(BigDecimal.valueOf(15.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        LoanOfferDTO loanOffer2 = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(112960).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9413.33))
                .rate(BigDecimal.valueOf(13.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        LoanOfferDTO loanOffer3 = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(111040).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9253.33))
                .rate(BigDecimal.valueOf(11.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();

        LoanOfferDTO loanOffer4 = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(109000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9083.33))
                .rate(BigDecimal.valueOf(9.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        List<LoanOfferDTO> expected = Stream.of(loanOffer1, loanOffer2, loanOffer3, loanOffer4)
                .sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed())
                .collect(Collectors.toList());

        when(applicationService.application(any())).thenReturn(expected);

        mockMvc.perform( post("/application/")
                        .content(objectMapper.writeValueAsString(getLoanApplicationRequestDTO()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void applyApplication() throws Exception {
        mockMvc.perform( put("/application/offer")
                        .content(objectMapper.writeValueAsString(getLoanOfferDTO()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(applicationService, times(1)).applyLoanOffer(getLoanOfferDTO());
    }
}