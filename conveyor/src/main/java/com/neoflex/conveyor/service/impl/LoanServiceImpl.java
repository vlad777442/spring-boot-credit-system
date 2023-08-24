package com.neoflex.conveyor.service.impl;

import com.neoflex.conveyor.config.properties.LoanProperties;
import com.neoflex.conveyor.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.api.response.LoanOfferDTO;
import com.neoflex.conveyor.exception.LoanApplicationException;
import com.neoflex.conveyor.service.LoanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanProperties loanProperties;

    @Override
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO) {
        log.info("STARTED GET LOAN OFFERS");

        return Stream.of(generateLoanOffer( false, false, requestDTO),
                generateLoanOffer(false, true, requestDTO),
                generateLoanOffer(true, false, requestDTO),
                generateLoanOffer(true, true, requestDTO))
                .sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed()).toList();
    }

    @Override
    public LoanOfferDTO generateLoanOffer(boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO requestDTO) {
        log.info("STARTED GENERATING LOAN OFFERS");
        isLoanApplicationValid(requestDTO);

        BigDecimal interestRate = loanProperties.getBaseInterestRate();
        BigDecimal loanAmount = requestDTO.getAmount();
        Integer term = requestDTO.getTerm();

        interestRate = calculateLoanRateByInsuranceClient(interestRate, isInsuranceEnabled);
        interestRate = calculateLoanRateBySalaryClient(interestRate, isSalaryClient);
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(loanProperties.getAnnualPeriod()), loanProperties.getScale(), loanProperties.getRoundingMode());
        BigDecimal totalInterest = loanAmount.multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(term)).divide(BigDecimal.valueOf(100), loanProperties.getScale(), loanProperties.getRoundingMode());
        BigDecimal totalAmount = loanAmount.add(totalInterest);
        BigDecimal monthlyPayment = calculateLoanMonthlyPayment(totalAmount, term);

        return LoanOfferDTO.builder()
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

        if (agePeriod.getYears() < loanProperties.getMinLoanAge()) {
            exceptions.add(new LoanApplicationException("The age does not meet a minimum requirement"));
        }

        if (!exceptions.isEmpty())
            throw new LoanApplicationException(exceptions.toString());
    }

    @Override
    public BigDecimal calculateLoanRateBySalaryClient(BigDecimal interestRate, boolean isSalaryClient) {
        log.info("CHECKING IF SALARY CLIENT");
        if (isSalaryClient) {
            return interestRate.subtract(loanProperties.getSalaryClientDiscount());
        }
        else {
            return interestRate.add(loanProperties.getNonSalaryClientAdd());
        }
    }

    @Override
    public BigDecimal calculateLoanRateByInsuranceClient(BigDecimal interestRate, boolean isInsuranceEnabled) {
        log.info("CHECKING IF INSURANCE CLIENT");
        if (isInsuranceEnabled) {
            return interestRate.subtract(loanProperties.getInsuranceClientDiscount());
        }
        else {
            return interestRate.add(loanProperties.getNonInsuranceClientAdd());
        }
    }

    @Override
    public BigDecimal calculateLoanMonthlyPayment(BigDecimal totalAmount, Integer term) {
        log.info("CALCULATING MONTHLY LOAN PAYMENT");
        return totalAmount.divide(BigDecimal.valueOf(term), loanProperties.getScale(), loanProperties.getRoundingMode());
    }

}
