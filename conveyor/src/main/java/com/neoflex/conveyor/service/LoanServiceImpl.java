package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.LoanApplicationRequestDTO;
import com.neoflex.conveyor.dto.LoanOfferDTO;
import com.neoflex.conveyor.exception.LoanApplicationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Log4j2
public class LoanServiceImpl implements LoanService {
    private static final BigDecimal BASE_INTEREST_RATE = BigDecimal.valueOf(12.0);
    private static final Integer MIN_LOAN_AGE = 18;
    private static final Integer ANNUAL_PERIOD = 12;

    @Override
    public List<LoanOfferDTO> getLoanOffers(LoanApplicationRequestDTO requestDTO) {
        log.info("STARTED GET LOAN OFFERS");
        List<LoanOfferDTO> list = new ArrayList<>();

        list.add(generateLoanOffer(1L, false, false, requestDTO));
        list.add(generateLoanOffer(2L, false, true, requestDTO));
        list.add(generateLoanOffer(3L, true, false, requestDTO));
        list.add(generateLoanOffer(4L, true, true, requestDTO));

        list.sort(Comparator.comparing(LoanOfferDTO::getRate).reversed());

        return list;
    }

    @Override
    public LoanOfferDTO generateLoanOffer(Long id, boolean isInsuranceEnabled, boolean isSalaryClient, LoanApplicationRequestDTO requestDTO) {
        log.info("STARTED GENERATING LOAN OFFERS");
        isLoanApplicationValid(requestDTO);

        BigDecimal interestRate = BASE_INTEREST_RATE;
        BigDecimal loanAmount = requestDTO.getAmount();
        Integer term = requestDTO.getTerm();

        interestRate = calculateLoanRateByInsuranceClient(interestRate, isInsuranceEnabled);
        interestRate = calculateLoanRateBySalaryClient(interestRate, isSalaryClient);
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(ANNUAL_PERIOD), 2, RoundingMode.HALF_UP);
        BigDecimal totalInterest = loanAmount.multiply(monthlyRate)
                .multiply(BigDecimal.valueOf(term)).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = loanAmount.add(totalInterest);
        BigDecimal monthlyPayment = calculateLoanMonthlyPayment(totalAmount, term);

        LoanOfferDTO offerDTO = LoanOfferDTO.builder()
                .applicationId(id)
                .requestedAmount(requestDTO.getAmount())
                .totalAmount(totalAmount)
                .term(requestDTO.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(interestRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

        return offerDTO;
    }

    private void isLoanApplicationValid(LoanApplicationRequestDTO requestDTO) {
        log.info("CHECKING IF LOAN APPLICATION IS VALID");
        List<LoanApplicationException> exceptions = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        Period agePeriod = Period.between(requestDTO.getBirthdate(), currentDate);

        if (!(agePeriod.getYears() >= MIN_LOAN_AGE)) {
            exceptions.add(new LoanApplicationException("The age does not meet a minimum requirement"));
        }

        if (!exceptions.isEmpty())
            throw new LoanApplicationException(exceptions.toString());
    }

    @Override
    public BigDecimal calculateLoanRateBySalaryClient(BigDecimal interestRate, boolean isSalaryClient) {
        log.info("CHECKING IF SALARY CLIENT");
        if (isSalaryClient) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(2.0));
        }
        else {
            interestRate = interestRate.add(BigDecimal.valueOf(2.0));
        }
        return interestRate;
    }

    @Override
    public BigDecimal calculateLoanRateByInsuranceClient(BigDecimal interestRate, boolean isInsuranceEnabled) {
        log.info("CHECKING IF INSURANCE CLIENT");
        if (isInsuranceEnabled) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(1.0));
        }
        else {
            interestRate = interestRate.add(BigDecimal.valueOf(1.0));
        }
        return interestRate;
    }

    @Override
    public BigDecimal calculateLoanMonthlyPayment(BigDecimal totalAmount, Integer term) {
        log.info("CALCULATING MONTHLY LOAN PAYMENT");
        BigDecimal monthlyPayment = totalAmount.divide(BigDecimal.valueOf(term), 2, RoundingMode.HALF_UP);
        return monthlyPayment;
    }

}
