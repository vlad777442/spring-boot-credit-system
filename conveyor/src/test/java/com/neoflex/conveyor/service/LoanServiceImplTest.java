package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.api.response.LoanOfferDTO;
import com.neoflex.conveyor.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class LoanServiceImplTest {
    @Autowired
    private LoanServiceImpl loanService;

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

    @Test
    void getLoanOffers() {
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
                .applicationId(3L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(112960).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9413.33))
                .rate(BigDecimal.valueOf(13.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        LoanOfferDTO loanOffer3 = LoanOfferDTO.builder()
                .applicationId(2L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(111040).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9253.33))
                .rate(BigDecimal.valueOf(11.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();

        LoanOfferDTO loanOffer4 = LoanOfferDTO.builder()
                .applicationId(4L)
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

        assertAll(
                () ->  assertEquals(expected, loanService.getLoanOffers(getLoanApplicationRequestDTO()))
        );
    }

    @Test
    void generateLoanOffer() {
        LoanOfferDTO expected = LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(115000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9583.33))
                .rate(BigDecimal.valueOf(15.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        LoanOfferDTO actual = loanService.generateLoanOffer(1L, false, false, getLoanApplicationRequestDTO());

        assertAll(
                () ->  assertEquals(expected, actual)
        );
    }

    @Test
    void calculateLoanRateBySalaryClient() {
        BigDecimal rate = loanService.calculateLoanRateBySalaryClient(BigDecimal.valueOf(5.0), true);

        assertAll(
                () ->  assertEquals(BigDecimal.valueOf(3.0), rate)
        );
    }

    @Test
    void calculateLoanRateByInsuranceClient() {
        BigDecimal rate = loanService.calculateLoanRateByInsuranceClient(BigDecimal.valueOf(5.0), true);

        assertAll(
                () ->  assertEquals(BigDecimal.valueOf(4.0), rate)
        );
    }

    @Test
    void calculateLoanMonthlyPayment() {
        BigDecimal expected = BigDecimal.valueOf(100000).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        BigDecimal actual = loanService.calculateLoanMonthlyPayment(BigDecimal.valueOf(100000), 12);

        assertAll(
                () ->  assertEquals(expected, actual)
        );
    }
}