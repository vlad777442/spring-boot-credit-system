package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.EmploymentDTO;
import com.neoflex.conveyor.dto.PaymentScheduleElement;
import com.neoflex.conveyor.dto.ScoringDataDTO;
import com.neoflex.conveyor.dto.enumType.EmploymentStatusType;
import com.neoflex.conveyor.dto.enumType.GenderType;
import com.neoflex.conveyor.dto.enumType.MaritalStatusType;
import com.neoflex.conveyor.dto.enumType.PositionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreditServiceImplTest {
    @InjectMocks
    private CreditServiceImpl creditService = new CreditServiceImpl();

    @BeforeEach
    void setUp() {
    }
    private EmploymentDTO employmentDTO = EmploymentDTO.builder()
            .employmentStatus(EmploymentStatusType.BUSINESS_OWNER)
            .employerINN("12345678910")
            .salary(BigDecimal.valueOf(10000000))
            .position(PositionType.SENIOR)
            .workExperienceTotal(36)
            .workExperienceCurrent(12)
            .build();

    @Test
    void calculateCreditDetails() {
        CreditDTO creditDTO = CreditDTO.builder()
                .amount(BigDecimal.valueOf(200000))
                .term(24)
                .rate(BigDecimal.valueOf(7.0))
                .psk(BigDecimal.valueOf(207057.6))
                .monthlyPayment(BigDecimal.valueOf(8777.832).setScale(8))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getAmount()).thenReturn(BigDecimal.valueOf(200000));
        when(scoringData.getTerm()).thenReturn(24);
        when(scoringData.getEmployment()).thenReturn(employmentDTO);
        when(scoringData.getGender()).thenReturn(GenderType.MALE);
        when(scoringData.getBirthdate()).thenReturn(LocalDate.of(1968, 10, 12));
        when(scoringData.getMaritalStatus()).thenReturn(MaritalStatusType.MARRIED);
        when(scoringData.getDependentAmount()).thenReturn(2);

        CreditDTO actual = creditService.calculateCreditDetails(scoringData);

        assertEquals(creditDTO.getAmount(), actual.getAmount());
        assertEquals(creditDTO.getMonthlyPayment(), actual.getMonthlyPayment());
    }

    @Test
    void calculateInterestRate() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getAmount()).thenReturn(BigDecimal.valueOf(200000));
        when(scoringData.getTerm()).thenReturn(24);
        when(scoringData.getEmployment()).thenReturn(employmentDTO);
        when(scoringData.getGender()).thenReturn(GenderType.MALE);
        when(scoringData.getBirthdate()).thenReturn(LocalDate.of(1968, 10, 12));
        when(scoringData.getMaritalStatus()).thenReturn(MaritalStatusType.MARRIED);
        when(scoringData.getDependentAmount()).thenReturn(2);

        BigDecimal rate = creditService.calculateInterestRate(scoringData);
        BigDecimal expected = BigDecimal.valueOf(5.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByPosition() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getEmployment()).thenReturn(employmentDTO);
        BigDecimal rate = creditService.calculateCreditRateByPosition(scoringData, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(6.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByMaritalStatus() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getMaritalStatus()).thenReturn(MaritalStatusType.MARRIED);
        BigDecimal rate = creditService.calculateCreditRateByMaritalStatus(scoringData, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(7.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByDependents() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getDependentAmount()).thenReturn(2);
        BigDecimal rate = creditService.calculateCreditRateByDependents(scoringData, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(11.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByAge() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getBirthdate()).thenReturn(LocalDate.of(1968, 10, 12));
        BigDecimal rate = creditService.calculateCreditRateByAge(scoringData, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(10.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculatePSK() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getAmount()).thenReturn(BigDecimal.valueOf(200000));
        when(scoringData.getTerm()).thenReturn(24);
        BigDecimal psk = creditService.calculatePSK(scoringData, BigDecimal.valueOf(7.0));
        BigDecimal expected = BigDecimal.valueOf(214821.792);
        assertEquals(expected.setScale(8), psk);
    }

    @Test
    void calculateMonthlyPayment() {
        ScoringDataDTO scoringData = mock(ScoringDataDTO.class);
        when(scoringData.getAmount()).thenReturn(BigDecimal.valueOf(200000));
        when(scoringData.getTerm()).thenReturn(24);
        BigDecimal rate = creditService.calculateMonthlyPayment(scoringData, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(9225.284);
        assertEquals(expected.setScale(8), rate);
    }

//    private ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
//            .amount(BigDecimal.valueOf(200000))
//            .term(24)
//            .firstName("Hugh")
//            .lastName("Jackman")
//            .middleName("Michael")
//            .gender(GenderType.FEMALE)
//            .birthdate(LocalDate.of(1968, 10, 12))
//            .passportSeries("1234")
//            .passportNumber("123456")
//            .passportIssueBranch("EXAMPLE")
//            .maritalStatus(MaritalStatusType.MARRIED)
//            .dependentAmount(2)
//            .employment(employmentDTO)
//            .account("548945")
//            .isInsuranceEnabled(false)
//            .isSalaryClient(false)
//            .build();
}