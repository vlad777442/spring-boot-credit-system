package com.neoflex.conveyor.service.impl;

import com.neoflex.conveyor.config.properties.CreditProperties;
import com.neoflex.conveyor.dto.api.request.ScoringDataDTO;
import com.neoflex.conveyor.dto.api.response.CreditDTO;
import com.neoflex.conveyor.dto.api.response.PaymentScheduleElement;
import com.neoflex.conveyor.dto.enums.EmploymentStatusType;
import com.neoflex.conveyor.dto.enums.GenderType;
import com.neoflex.conveyor.dto.enums.MaritalStatusType;
import com.neoflex.conveyor.dto.enums.PositionType;
import com.neoflex.conveyor.exception.ScoringDataException;
import com.neoflex.conveyor.service.CreditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final CreditProperties creditProperties;

    @Override
    public CreditDTO calculateCreditDetails(ScoringDataDTO scoringData) {
        log.info("STARTED CALCULATING CREDIT DETAILS");
        BigDecimal interestRate = calculateInterestRate(scoringData);
        BigDecimal psk = calculatePSK(scoringData, interestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(scoringData, interestRate);
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(creditProperties.getAnnualPeriod()), creditProperties.getScale(), creditProperties.getRoundingMode()).divide(BigDecimal.valueOf(100), creditProperties.getScale(), creditProperties.getRoundingMode());

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
        BigDecimal interestRate = creditProperties.getBaseInterestRate();
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
            interestRate = interestRate.subtract(creditProperties.getMiddlePositionDiscount());
        }
        else if (type == PositionType.SENIOR) {
            interestRate = interestRate.subtract(creditProperties.getSeniorPositionDiscount());
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByMaritalStatus(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING MARITAL STATUS");
        MaritalStatusType type = scoringDataDTO.getMaritalStatus();

        if (type == MaritalStatusType.MARRIED) {
            interestRate = interestRate.subtract(creditProperties.getMarriedDiscount());
        }
        else if (type == MaritalStatusType.DIVORCED) {
            interestRate = interestRate.add(creditProperties.getDivorcedAdd());
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByDependents(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING DEPENDENTS");
        if (scoringDataDTO.getDependentAmount() > 1) {
            interestRate = interestRate.add(creditProperties.getDependentsAdd());
        }

        return interestRate;
    }

    @Override
    public BigDecimal calculateCreditRateByAge(ScoringDataDTO scoringDataDTO, BigDecimal interestRate) {
        log.info("CHECKING AGE");
        LocalDate currentDate = LocalDate.now();
        Period age = Period.between(scoringDataDTO.getBirthdate(), currentDate);

        if (scoringDataDTO.getGender() == GenderType.FEMALE && age.getYears() >= 35 && age.getYears() <= 60) {
            interestRate = interestRate.subtract(creditProperties.getFemaleBetween35And60Discount());
        }
        else if (scoringDataDTO.getGender() == GenderType.MALE && age.getYears() >= 30 && age.getYears() <= 55) {
            interestRate = interestRate.subtract(creditProperties.getMaleBetween30And55Discount());
        }
        else if (scoringDataDTO.getGender() == GenderType.NON_BINARY) {
            interestRate = interestRate.add(creditProperties.getNonBinaryAdd());
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
                .multiply(BigDecimal.valueOf(creditProperties.getMinSalaryCount())).intValue()) {
            exceptions.add(new ScoringDataException("The salary is too low"));
        }

        LocalDate currentDate = LocalDate.now();
        Period agePeriod = Period.between(scoringDataDTO.getBirthdate(), currentDate);

        if (agePeriod.getYears() < creditProperties.getMinCreditAge() || agePeriod.getYears() > creditProperties.getMaxCreditAge()) {
            exceptions.add(new ScoringDataException("The age is not appropriate"));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceCurrent() < creditProperties.getMinCurrentWorkExperience()) {
            exceptions.add(new ScoringDataException("The current work experience is small"));
        }

        if (scoringDataDTO.getEmployment().getWorkExperienceTotal() < creditProperties.getMinCurrentWorkExperience()) {
            exceptions.add(new ScoringDataException("The total work experience is small"));
        }

        if (!exceptions.isEmpty())
            throw new ScoringDataException(exceptions.toString());

    }

    @Override
    public BigDecimal calculatePSK(ScoringDataDTO scoringData, BigDecimal interestRate) {
        log.info("CALCULATING PSK");
        return calculateMonthlyPayment(scoringData, interestRate)
                .multiply(BigDecimal.valueOf(scoringData.getTerm())).setScale(creditProperties.getScale(), creditProperties.getRoundingMode());
    }

    @Override
    public BigDecimal calculateMonthlyPayment(ScoringDataDTO scoringData, BigDecimal interestRate) {
        log.info("CALCULATING MONTHLY PAYMENT");
        BigDecimal monthlyRate = interestRate.divide(BigDecimal.valueOf(creditProperties.getAnnualPeriod()), 4, creditProperties.getRoundingMode()).divide(BigDecimal.valueOf(100), 4, creditProperties.getRoundingMode());

        BigDecimal annualCoeff = monthlyRate.multiply(
                        (monthlyRate.add(BigDecimal.ONE)).pow(scoringData.getTerm())
                        .divide(
                                (monthlyRate.add(BigDecimal.ONE).pow(scoringData.getTerm()))
                                .subtract(BigDecimal.ONE)
                        , 4, creditProperties.getRoundingMode())
        );

        return scoringData.getAmount().multiply(annualCoeff).setScale(creditProperties.getScale(), creditProperties.getRoundingMode());
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
            BigDecimal interestPayment = remainingPayment.multiply(monthlyRate).setScale(creditProperties.getScale(), creditProperties.getRoundingMode());
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment).setScale(creditProperties.getScale(), creditProperties.getRoundingMode());
            remainingPayment = remainingPayment.subtract(debtPayment).setScale(creditProperties.getScale(), creditProperties.getRoundingMode());

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
