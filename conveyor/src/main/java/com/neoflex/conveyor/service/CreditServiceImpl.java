package com.neoflex.conveyor.service;

import com.neoflex.conveyor.dto.*;
import com.neoflex.conveyor.dto.enumType.EmploymentStatusType;
import com.neoflex.conveyor.dto.enumType.GenderType;
import com.neoflex.conveyor.dto.enumType.MaritalStatusType;
import com.neoflex.conveyor.dto.enumType.PositionType;
import com.neoflex.conveyor.exception.ScoringDataException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class CreditServiceImpl implements CreditService {
    private static final BigDecimal BASE_INTEREST_RATE = BigDecimal.valueOf(14.0);
    private static final Integer ANNUAL_PERIOD = 12;
    private static final Integer MIN_SALARY_COUNT = 20;
    private static final Integer MIN_CREDIT_AGE = 20;
    private static final Integer MAX_CREDIT_AGE = 60;
    private static final Integer MIN_TOTAL_WORK_EXPERIENCE = 12;
    private static final Integer MIN_CURRENT_WORK_EXPERIENCE = 3;

    @Override
    public CreditDTO calculateCreditDetails(ScoringDataDTO scoringData) {
        log.info("STARTED CALCULATING CREDIT DETAILS");
        BigDecimal interestRate = calculateInterestRate(scoringData);
        BigDecimal psk = calculatePSK(scoringData, interestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(scoringData, interestRate);
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(ANNUAL_PERIOD), 2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        List<PaymentScheduleElement> paymentSchedule = generatePaymentSchedule(scoringData, monthlyPayment, monthlyRate);

        CreditDTO creditDTO = CreditDTO.builder()
                .amount(scoringData.getAmount())
                .term(scoringData.getTerm())
                .rate(interestRate)
                .psk(psk)
                .monthlyPayment(monthlyPayment)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();

        return creditDTO;
    }

    @Override
    public BigDecimal calculateInterestRate(ScoringDataDTO scoringData) {
        log.info("CALCULATING INTEREST RATE");
        BigDecimal interestRate = BASE_INTEREST_RATE;
        isCreditApplicationValid(scoringData);

        interestRate = calculateCreditRateByPosition(scoringData, interestRate);
        interestRate = calculateCreditRateByMaritalStatus(scoringData, interestRate);
        interestRate = calculateCreditRateByDependents(scoringData, interestRate);
        interestRate = calculateCreditRateByAge(scoringData, interestRate);

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByPosition(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING EMPLOYMENT STATUS");
        PositionType type = scoringDataDTO.getEmployment().getPosition();
        if (type == PositionType.MIDDLE) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(2.0));
        }
        if (type == PositionType.SENIOR) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(4.0));
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByMaritalStatus(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING MARITAL STATUS");
        MaritalStatusType type = scoringDataDTO.getMaritalStatus();
        if (type == MaritalStatusType.MARRIED) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(3.0));
        }
        if (type == MaritalStatusType.DIVORCED) {
            interestRate = interestRate.add(BigDecimal.valueOf(1.0));
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByDependents(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING DEPENDENTS");
        if (scoringDataDTO.getDependentAmount() > 1) {
            interestRate = interestRate.add(BigDecimal.valueOf(1.0));
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByAge(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING AGE");
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(scoringDataDTO.getBirthdate(), currentDate);

        if (scoringDataDTO.getGender() == GenderType.FEMALE && age.getYears() >= 35 && age.getYears() <= 60) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(3.0));
        }
        else if (scoringDataDTO.getGender() == GenderType.MALE && age.getYears() >= 30 && age.getYears() <= 55) {
            interestRate = interestRate.subtract(BigDecimal.valueOf(3.0));
        }
        else if (scoringDataDTO.getGender() == GenderType.NON_BINARY) {
            interestRate = interestRate.add(BigDecimal.valueOf(3.0));
        }

        return interestRate;
    }

    @Override
    public void isCreditApplicationValid(ScoringDataDTO scoringDataDTO) {
        log.info("VALIDATING CREDIT APPLICATION");
        List<ScoringDataException> exceptions = new ArrayList<>();

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatusType.UNEMPLOYED) {
            exceptions.add(new ScoringDataException("Unemployed status"));
        }
        if (scoringDataDTO.getAmount().intValue() > scoringDataDTO.getEmployment().getSalary()
                .multiply(BigDecimal.valueOf(MIN_SALARY_COUNT)).intValue()) {
            exceptions.add(new ScoringDataException("The salary is too low"));
        }

        LocalDate currentDate = LocalDate.now();
        Period agePeriod = Period.between(scoringDataDTO.getBirthdate(), currentDate);

        if (agePeriod.getYears() < MIN_CREDIT_AGE || agePeriod.getYears() > MAX_CREDIT_AGE) {
            exceptions.add(new ScoringDataException("The age is not appropriate"));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < MIN_CURRENT_WORK_EXPERIENCE) {
            exceptions.add(new ScoringDataException("The current work experience is small"));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < MIN_TOTAL_WORK_EXPERIENCE) {
            exceptions.add(new ScoringDataException("The total work experience is small"));
        }

        if (!exceptions.isEmpty())
            throw new ScoringDataException(exceptions.toString());

    }

    @Override
    public BigDecimal calculatePSK(ScoringDataDTO scoringData, BigDecimal interestRate) {
        log.info("CALCULATING PSK");
        BigDecimal psk = calculateMonthlyPayment(scoringData, interestRate)
                .multiply(BigDecimal.valueOf(scoringData.getTerm()));

        return psk;
    }

    @Override
    public BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringData, BigDecimal interestRate) {
        log.info("CALCULATING MONTHLY PAYMENT");
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(ANNUAL_PERIOD), 2, RoundingMode.HALF_UP).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal annualCoeff = monthlyRate.multiply(
                        (monthlyRate.add(BigDecimal.ONE)).pow(scoringData.getTerm())
                        .divide(
                                (monthlyRate.add(BigDecimal.ONE).pow(scoringData.getTerm()))
                                .subtract(BigDecimal.ONE)
                        , 4, RoundingMode.HALF_EVEN)
        );
        BigDecimal monthlyPayment = scoringData.getAmount().multiply(annualCoeff);

        return monthlyPayment;
    }

    @Override
    public List<PaymentScheduleElement> generatePaymentSchedule(ScoringDataDTO scoringData, BigDecimal monthlyPayment,
                                                                 BigDecimal monthlyRate) {
        log.info("GENERATING PAYMENT SCHEDULE");
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateOfPayment;
        BigDecimal remainingPayment = scoringData.getAmount();

        for (int month = 1; month <= scoringData.getTerm(); month++) {

            dateOfPayment = currentDate.plusMonths(month);
            BigDecimal interestPayment = remainingPayment.multiply(monthlyRate);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);
            remainingPayment = remainingPayment.subtract(debtPayment);

            PaymentScheduleElement element = PaymentScheduleElement.builder()
                    .number(month)
                    .totalPayment(monthlyPayment.multiply(BigDecimal.valueOf(month)))
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingPayment)
                    .date(dateOfPayment)
                    .build();

            schedule.add(element);
        }
        return schedule;
    }


}
