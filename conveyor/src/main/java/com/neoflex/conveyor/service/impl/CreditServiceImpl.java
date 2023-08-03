package com.neoflex.conveyor.service.impl;

import com.neoflex.conveyor.dto.api.request.ScoringDataDTO;
import com.neoflex.conveyor.dto.api.response.CreditDTO;
import com.neoflex.conveyor.dto.api.response.PaymentScheduleElement;
import com.neoflex.conveyor.dto.enums.EmploymentStatusType;
import com.neoflex.conveyor.dto.enums.GenderType;
import com.neoflex.conveyor.dto.enums.MaritalStatusType;
import com.neoflex.conveyor.dto.enums.PositionType;
import com.neoflex.conveyor.exception.ScoringDataException;
import com.neoflex.conveyor.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CreditServiceImpl implements CreditService {
    @Value("${credit.service.BASE_INTEREST_RATE}")
    private BigDecimal baseInterestRate;

    @Value("${credit.service.ANNUAL_PERIOD}")
    private Integer annualPeriod;

    @Value("${credit.service.MIN_SALARY_COUNT}")
    private Integer minSalaryCount;

    @Value("${credit.service.MIN_CREDIT_AGE}")
    private Integer minCreditAge;

    @Value("${credit.service.MAX_CREDIT_AGE}")
    private Integer maxCreditAge;

    @Value("${credit.service.MIN_TOTAL_WORK_EXPERIENCE}")
    private Integer minTotalWorkExperience;

    @Value("${credit.service.MIN_CURRENT_WORK_EXPERIENCE}")
    private Integer minCurrentWorkExperience;

    @Value("${credit.service.scale}")
    private Integer scale;

    @Value("${credit.service.roundingMode}")
    private RoundingMode roundingMode;

    @Value("${credit.service.middlePositionDiscount}")
    private BigDecimal middlePositionDiscount;

    @Value("${credit.service.seniorPositionDiscount}")
    private BigDecimal seniorPositionDiscount;

    @Value("${credit.service.marriedDiscount}")
    private BigDecimal marriedDiscount;

    @Value("${credit.service.divorcedAdd}")
    private BigDecimal divorcedAdd;

    @Value("${credit.service.dependentsAdd}")
    private BigDecimal dependentsAdd;

    @Value("${credit.service.femaleBetween35And60Discount}")
    private BigDecimal femaleBetween35And60Discount;

    @Value("${credit.service.maleBetween30And55Discount}")
    private BigDecimal maleBetween30And55Discount;

    @Value("${credit.service.nonBinaryAdd}")
    private BigDecimal nonBinaryAdd;

    @Override
    public CreditDTO calculateCreditDetails(ScoringDataDTO scoringData) {
        log.info("STARTED CALCULATING CREDIT DETAILS");
        BigDecimal interestRate = calculateInterestRate(scoringData);
        BigDecimal psk = calculatePSK(scoringData, interestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(scoringData, interestRate);
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(annualPeriod), 2, roundingMode).divide(BigDecimal.valueOf(100), scale, roundingMode);

        List<PaymentScheduleElement> paymentSchedule = generatePaymentSchedule(scoringData, monthlyPayment, monthlyRate);

        return CreditDTO.builder()
                .amount(scoringData.getAmount())
                .term(scoringData.getTerm())
                .rate(interestRate)
                .psk(psk)
                .monthlyPayment(monthlyPayment)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();
    }

    @Override
    public BigDecimal calculateInterestRate(ScoringDataDTO scoringData) {
        log.info("CALCULATING INTEREST RATE");
        BigDecimal interestRate = baseInterestRate;
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
            interestRate = interestRate.subtract(middlePositionDiscount);
        }
        else if (type == PositionType.SENIOR) {
            interestRate = interestRate.subtract(seniorPositionDiscount);
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByMaritalStatus(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING MARITAL STATUS");
        MaritalStatusType type = scoringDataDTO.getMaritalStatus();

        if (type == MaritalStatusType.MARRIED) {
            interestRate = interestRate.subtract(marriedDiscount);
        }
        else if (type == MaritalStatusType.DIVORCED) {
            interestRate = interestRate.add(divorcedAdd);
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByDependents(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING DEPENDENTS");
        if (scoringDataDTO.getDependentAmount() > 1) {
            interestRate = interestRate.add(dependentsAdd);
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByAge(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING AGE");
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(scoringDataDTO.getBirthdate(), currentDate);

        if (scoringDataDTO.getGender() == GenderType.FEMALE && age.getYears() >= 35 && age.getYears() <= 60) {
            interestRate = interestRate.subtract(femaleBetween35And60Discount);
        }
        else if (scoringDataDTO.getGender() == GenderType.MALE && age.getYears() >= 30 && age.getYears() <= 55) {
            interestRate = interestRate.subtract(maleBetween30And55Discount);
        }
        else if (scoringDataDTO.getGender() == GenderType.NON_BINARY) {
            interestRate = interestRate.add(nonBinaryAdd);
        }

        return interestRate;
    }

    private void isCreditApplicationValid(ScoringDataDTO scoringDataDTO) {
        log.info("VALIDATING CREDIT APPLICATION");
        List<ScoringDataException> exceptions = new ArrayList<>();

        if (scoringDataDTO.getEmployment().getEmploymentStatus() == EmploymentStatusType.UNEMPLOYED) {
            exceptions.add(new ScoringDataException("Unemployed status"));
        }

        if (scoringDataDTO.getAmount().intValue() > scoringDataDTO.getEmployment().getSalary()
                .multiply(BigDecimal.valueOf(minSalaryCount)).intValue()) {
            exceptions.add(new ScoringDataException("The salary is too low"));
        }

        LocalDate currentDate = LocalDate.now();
        Period agePeriod = Period.between(scoringDataDTO.getBirthdate(), currentDate);

        if (agePeriod.getYears() < minCreditAge || agePeriod.getYears() > maxCreditAge) {
            exceptions.add(new ScoringDataException("The age is not appropriate"));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < minCurrentWorkExperience) {
            exceptions.add(new ScoringDataException("The current work experience is small"));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < minTotalWorkExperience) {
            exceptions.add(new ScoringDataException("The total work experience is small"));
        }

        if (!exceptions.isEmpty())
            throw new ScoringDataException(exceptions.toString());

    }

    @Override
    public BigDecimal calculatePSK(ScoringDataDTO scoringData, BigDecimal interestRate) {
        log.info("CALCULATING PSK");
        return calculateMonthlyPayment(scoringData, interestRate)
                .multiply(BigDecimal.valueOf(scoringData.getTerm())).setScale(scale, roundingMode);
    }

    @Override
    public BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringData, BigDecimal interestRate) {
        log.info("CALCULATING MONTHLY PAYMENT");
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(annualPeriod), 4, roundingMode).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal annualCoeff = monthlyRate.multiply(
                        (monthlyRate.add(BigDecimal.ONE)).pow(scoringData.getTerm())
                        .divide(
                                (monthlyRate.add(BigDecimal.ONE).pow(scoringData.getTerm()))
                                .subtract(BigDecimal.ONE)
                        , 4, roundingMode)
        );

        return scoringData.getAmount().multiply(annualCoeff).setScale(scale, roundingMode);
    }

    private List<PaymentScheduleElement> generatePaymentSchedule(ScoringDataDTO scoringData, BigDecimal monthlyPayment,
                                                                 BigDecimal monthlyRate) {
        log.info("GENERATING PAYMENT SCHEDULE");
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        LocalDate dateOfPayment;
        BigDecimal remainingPayment = scoringData.getAmount();

        for (int month = 1; month <= scoringData.getTerm(); month++) {

            dateOfPayment = currentDate.plusMonths(month);
            BigDecimal interestPayment = remainingPayment.multiply(monthlyRate).setScale(scale, roundingMode);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(scale, roundingMode);
            remainingPayment = remainingPayment.subtract(debtPayment).setScale(scale, roundingMode);

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
