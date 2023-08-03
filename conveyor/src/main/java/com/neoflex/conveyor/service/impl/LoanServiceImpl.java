package com.neoflex.conveyor.service.impl;

import com.neoflex.conveyor.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.api.response.LoanOfferDTO;
import com.neoflex.conveyor.exception.LoanApplicationException;
import com.neoflex.conveyor.service.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
public class LoanServiceImpl implements LoanService {
    @Value("${loan.service.base-interest-rate}")
    private BigDecimal baseInterestRate;

    @Value("${loan.service.min-loan-age}")
    private Integer minLoanAge;

    @Value("${loan.service.annual-period}")
    private Integer annualPeriod;

    @Value("${loan.service.scale}")
    private Integer scale;

    @Value("${loan.service.rounding-mode}")
    private RoundingMode roundingMode;

    @Value("${loan.service.salary-client-discount}")
    private BigDecimal salaryClientDiscount;

    @Value("${loan.service.non-salary-client-add}")
    private BigDecimal nonSalaryClientAdd;

    @Value("${loan.service.insurance-client-discount}")
    private BigDecimal insuranceClientDiscount;

    @Value("${loan.service.non-insurance-client-add}")
    private BigDecimal nonInsuranceClientAdd;

    @Override
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO) {
        log.info("STARTED GET LOAN OFFERS");

        return Stream.of(generateLoanOffer(1L, false, false, requestDTO),
                generateLoanOffer(2L, false, true, requestDTO),
                generateLoanOffer(3L, true, false, requestDTO),
                generateLoanOffer(4L, true, true, requestDTO))
                .sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed()).toList();
    }

    @Override
    public LoanOfferDTO generateLoanOffer(Long id, boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO requestDTO) {
        log.info("STARTED GENERATING LOAN OFFERS");
        isLoanApplicationValid(requestDTO);

        BigDecimal interestRate = baseInterestRate;
        BigDecimal loanAmount = requestDTO.getAmount();
        Integer term = requestDTO.getTerm();

        interestRate = calculateLoanRateByInsuranceClient(interestRate, isInsuranceEnabled);
        interestRate = calculateLoanRateBySalaryClient(interestRate, isSalaryClient);
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(annualPeriod), scale, roundingMode);
        BigDecimal totalInterest = loanAmount.multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(term)).divide(BigDecimal.valueOf(100), scale, roundingMode);
        BigDecimal totalAmount = loanAmount.add(totalInterest);
        BigDecimal monthlyPayment = calculateLoanMonthlyPayment(totalAmount, term);

        return LoanOfferDTO.builder()
                .applicationId(id)
                .requestedAmount(requestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(requestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(interestRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }

    private void isLoanApplicationValid(LoanApplicationRequestDTO requestDTO) {
        log.info("CHECKING IF LOAN APPLICATION IS VALID");
        List<LoanApplicationException> exceptions = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        Period agePeriod = Period.between(requestDTO.getBirthdate(), currentDate);

        if (agePeriod.getYears() < minLoanAge) {
            exceptions.add(new LoanApplicationException("The age does not meet a minimum requirement"));
        }

        if (!exceptions.isEmpty())
            throw new LoanApplicationException(exceptions.toString());
    }

    @Override
    public BigDecimal calculateLoanRateBySalaryClient(BigDecimal interestRate, boolean isSalaryClient) {
        log.info("CHECKING IF SALARY CLIENT");
        if (isSalaryClient) {
            return interestRate.subtract(salaryClientDiscount);
        }
        else {
            return interestRate.add(nonSalaryClientAdd);
        }
    }

    @Override
    public BigDecimal calculateLoanRateByInsuranceClient(BigDecimal interestRate, boolean isInsuranceEnabled) {
        log.info("CHECKING IF INSURANCE CLIENT");
        if (isInsuranceEnabled) {
            return interestRate.subtract(insuranceClientDiscount);
        }
        else {
            return interestRate.add(nonInsuranceClientAdd);
        }
    }

    @Override
    public BigDecimal calculateLoanMonthlyPayment(BigDecimal totalAmount, Integer term) {
        log.info("CALCULATING MONTHLY LOAN PAYMENT");
        return totalAmount.divide(BigDecimal.valueOf(term), scale, roundingMode);
    }

}
