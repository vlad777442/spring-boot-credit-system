package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.api.response.CreditDTO;
import com.neoflex.conveyor.dto.api.request.EmploymentDTO;
import com.neoflex.conveyor.dto.api.request.ScoringDataDTO;
import com.neoflex.conveyor.dto.enums.EmploymentStatusType;
import com.neoflex.conveyor.dto.enums.GenderType;
import com.neoflex.conveyor.dto.enums.MaritalStatusType;
import com.neoflex.conveyor.dto.enums.PositionType;
import com.neoflex.conveyor.service.impl.CreditServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CreditServiceImplTest {
    @Autowired
    private CreditServiceImpl creditService;

    private EmploymentDTO getEmploymentDTO() {
        return EmploymentDTO.builder()
                .employmentStatus(EmploymentStatusType.BUSINESS_OWNER)
                .employerINN("12345678910")
                .salary(BigDecimal.valueOf(10000000))
                .position(PositionType.SENIOR)
                .workExperienceTotal(36)
                .workExperienceCurrent(12)
                .build();
    }

    private ScoringDataDTO getScoringDataDTO() {
        return ScoringDataDTO.builder()
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
                .employment(getEmploymentDTO())
                .account("548945")
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
    }

//    @BeforeEach
//    public void setUp() {
//        ReflectionTestUtils.setField();
//    }

    @Test
    void calculateCreditDetails() {
        CreditDTO creditDTO = CreditDTO.builder()
                .amount(BigDecimal.valueOf(200000))
                .term(24)
                .rate(BigDecimal.valueOf(9.0))
                .psk(BigDecimal.valueOf(210667.92))
                .monthlyPayment(BigDecimal.valueOf(8777.83))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        CreditDTO actual = creditService.calculateCreditDetails(getScoringDataDTO());

        assertAll(
                () -> assertEquals(creditDTO.getAmount(), actual.getAmount()),
                () -> assertEquals(creditDTO.getPsk(), actual.getPsk()),
                () -> assertEquals(creditDTO.getMonthlyPayment(), actual.getMonthlyPayment())
        );
    }

    @Test
    void calculateInterestRate() {
        BigDecimal rate = creditService.calculateInterestRate(getScoringDataDTO());
        BigDecimal expected = BigDecimal.valueOf(5.0);

        assertAll(
                () -> assertEquals(expected, rate)
        );
    }

    @Test
    void calculateCreditRateByPosition() {
        BigDecimal rate = creditService.calculateCreditRateByPosition(getScoringDataDTO(), BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(6.0);

        assertAll(
                () -> assertEquals(expected, rate)
        );
    }

    @Test
    void calculateCreditRateByMaritalStatus() {
        BigDecimal rate = creditService.calculateCreditRateByMaritalStatus(getScoringDataDTO(), BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(7.0);

        assertAll(
                () ->  assertEquals(expected, rate)
        );
    }

    @Test
    void calculateCreditRateByDependents() {
        BigDecimal rate = creditService.calculateCreditRateByDependents(getScoringDataDTO(), BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(11.0);

        assertAll(
                () ->  assertEquals(expected, rate)
        );
    }

    @Test
    void calculateCreditRateByAge() {
        BigDecimal rate = creditService.calculateCreditRateByAge(getScoringDataDTO(), BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(7.0);

        assertAll(
                () ->  assertEquals(expected, rate)
        );
    }

    @Test
    void calculatePSK() {
        BigDecimal psk = creditService.calculatePSK(getScoringDataDTO(), BigDecimal.valueOf(7.0));
        BigDecimal expected = BigDecimal.valueOf(214821.84);

        assertAll(
                () ->  assertEquals(expected, psk)
        );
    }

    @Test
    void calculateMonthlyPayment() {
        BigDecimal rate = creditService.calculateMonthlyPayment(getScoringDataDTO(), BigDecimal.valueOf(10.0));
        BigDecimal expected = BigDecimal.valueOf(9225.28);

        assertAll(
                () ->  assertEquals(expected, rate)
        );
    }
}