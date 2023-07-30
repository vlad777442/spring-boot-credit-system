package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.CreditDTO;
import com.neoflex.conveyor.dto.EmploymentDTO;
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

import static org.junit.jupiter.api.Assertions.*;

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
    private ScoringDataDTO scoringDataDTO = ScoringDataDTO.builder()
            .amount(BigDecimal.valueOf(200000))
            .term(24)
            .firstName("Hugh")
            .lastName("Jackman")
            .middleName("Michael")
            .gender(GenderType.FEMALE)
            .birthdate(LocalDate.of(1968, 10, 12))
            .passportSeries("1234")
            .passportNumber("123456")
            .passportIssueBranch("EXAMPLE")
            .maritalStatus(MaritalStatusType.MARRIED)
            .dependentAmount(2)
            .employment(employmentDTO)
            .account("548945")
            .isInsuranceEnabled(false)
            .isSalaryClient(false)
            .build();
    @Test
    void calculateCreditDetails() {
        CreditDTO creditDTO = CreditDTO.builder()
                .amount(BigDecimal.valueOf(200000))
                .term(12)
                .rate(BigDecimal.valueOf(7.0))
                .psk(BigDecimal.valueOf(207057.6))
                .monthlyPayment(BigDecimal.valueOf(8777.832).setScale(8))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        CreditDTO actual = creditService.calculateCreditDetails(scoringDataDTO);

        assertEquals(creditDTO.getAmount(), actual.getAmount());
        assertEquals(creditDTO.getMonthlyPayment(), actual.getMonthlyPayment());
    }

    @Test
    void calculateInterestRate() {
        BigDecimal rate = creditService.calculateInterestRate(scoringDataDTO);
        BigDecimal expected = BigDecimal.valueOf(5.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByPosition() {
        BigDecimal rate = creditService.calculateCreditRateByPosition(scoringDataDTO, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(6.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByMaritalStatus() {
        BigDecimal rate = creditService.calculateCreditRateByMaritalStatus(scoringDataDTO, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(7.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByDependents() {
        BigDecimal rate = creditService.calculateCreditRateByDependents(scoringDataDTO, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(11.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculateCreditRateByAge() {
        BigDecimal rate = creditService.calculateCreditRateByAge(scoringDataDTO, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(7.0);
        assertEquals(expected, rate);
    }

    @Test
    void calculatePSK() {
        BigDecimal psk = creditService.calculatePSK(scoringDataDTO, BigDecimal.valueOf(7.0));
        BigDecimal expected = BigDecimal.valueOf(214821.792);
        assertEquals(expected.setScale(8), psk);
    }

    @Test
    void calculateMonthlyPayment() {
        BigDecimal rate = creditService.calculateMonthlyPayment(scoringDataDTO, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(9225.284);
        assertEquals(expected.setScale(8), rate);
    }

    @Test
    void generatePaymentSchedule() {
        BigDecimal rate = creditService.calculateMonthlyPayment(scoringDataDTO, BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(9225.284);
        assertEquals(expected.setScale(8), rate);
    }
}