package com.neoflex.deal.service;

import com.neoflex.deal.client.ConveyorClient;
import com.neoflex.deal.dto.api.request.EmploymentDTO;
import com.neoflex.deal.dto.api.request.FinishRegistrationRequestDTO;
import com.neoflex.deal.dto.api.request.LoanApplicationRequestDTO;
import com.neoflex.deal.dto.api.request.ScoringDataDTO;
import com.neoflex.deal.dto.api.response.CreditDTO;
import com.neoflex.deal.dto.api.response.LoanOfferDTO;
import com.neoflex.deal.dto.enums.GenderType;
import com.neoflex.deal.dto.enums.MaritalStatusType;
import com.neoflex.deal.dto.enums.PositionType;
import com.neoflex.deal.mapper.CreditMapper;
import com.neoflex.deal.mapper.EmploymentMapper;
import com.neoflex.deal.mapper.ScoringDataMapper;
import com.neoflex.deal.mapper.impl.LoanOfferMapperMyImpl;
import com.neoflex.deal.model.*;
import com.neoflex.deal.model.enums.ApplicationStatus;
import com.neoflex.deal.model.enums.ChangeType;
import com.neoflex.deal.model.enums.EmploymentStatus;
import com.neoflex.deal.repository.ApplicationRepository;
import com.neoflex.deal.repository.ClientRepository;
import com.neoflex.deal.service.impl.DealServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealServiceTest {
    @InjectMocks
    private DealServiceImpl dealService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ConveyorClient conveyorClient;

    @Spy
    private EmploymentMapper employmentMapper;

    @Spy
    private CreditMapper creditMapper;

    @Spy
    private LoanOfferMapperMyImpl loanOfferMapper;

    @Spy
    ScoringDataMapper scoringDataMapper;

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

    private Client getClient() {
        Passport passport = Passport.builder()
                .series("1234")
                .number("123456")
                .build();

        return Client.builder()
                .lastName("Williams")
                .firstName("Sarah")
                .middleName("Alison")
                .birthDate(LocalDate.of(1995, 10, 15))
                .email("sarah@example.com")
                .passport(passport)
                .build();
    }

    private Application getApplication() {
        ArrayList<StatusHistory> statusHistories = new ArrayList<>();

        StatusHistory statusHistory =  StatusHistory.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();

        statusHistories.add(statusHistory);

        return Application.builder()
                .applicationId(1L)
                .client(getClient())
                .credit(getCredit())
                .creationDate(LocalDateTime.now())
                .status(ApplicationStatus.PREAPPROVAL)
                .statusHistory(statusHistories)
                .build();
    }

    private LoanOfferDTO getLoanOfferDTO() {
        return LoanOfferDTO.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(115000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9583.33))
                .rate(BigDecimal.valueOf(15.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
    }

    private FinishRegistrationRequestDTO getFinishRegistrationRequestDTO() {
        return FinishRegistrationRequestDTO.builder()
                .gender(GenderType.FEMALE)
                .maritalStatus(MaritalStatusType.SINGLE)
                .dependentAmount(1)
                .passportIssueDate(LocalDate.of(2005, 9, 10))
                .passportIssueBrach("Department")
                .account("12345")
                .build();
    }

    private CreditDTO getCreditDTO() {
        return CreditDTO.builder()
                .amount(BigDecimal.valueOf(200000))
                .term(24)
                .rate(BigDecimal.valueOf(12))
                .psk(BigDecimal.valueOf(225950.4))
                .build();
    }

    private Credit getCredit() {
        return Credit.builder()
                .amount(BigDecimal.valueOf(200000))
                .psk(BigDecimal.valueOf(225950.4))
                .term(24)
                .rate(BigDecimal.valueOf(12))
                .monthlyPayment(BigDecimal.valueOf(9414.6))
                .paymentSchedule(new ArrayList<>())
                .build();
    }

    private EmploymentDTO getEmploymentDTO() {
        return EmploymentDTO.builder()
                .employmentStatus(EmploymentStatus.BUSINESS_OWNER)
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

    @Test
    void getLoanOffers() {
        LoanOffer loanOffer1 = LoanOffer.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(115000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9583.33))
                .rate(BigDecimal.valueOf(15.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();

        LoanOffer loanOffer2 = LoanOffer.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(112960).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9413.33))
                .rate(BigDecimal.valueOf(13.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .build();

        LoanOffer loanOffer3 = LoanOffer.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(111040).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9253.33))
                .rate(BigDecimal.valueOf(11.0))
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .build();

        LoanOffer loanOffer4 = LoanOffer.builder()
                .applicationId(1L)
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(109000).setScale(2, RoundingMode.HALF_UP))
                .term(12)
                .monthlyPayment(BigDecimal.valueOf(9083.33))
                .rate(BigDecimal.valueOf(9.0))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        List<LoanOffer> expected = Stream.of(loanOffer1, loanOffer2, loanOffer3, loanOffer4)
                .sorted(Comparator.comparing(LoanOffer::getRate).reversed())
                .collect(Collectors.toList());

        when(applicationRepository.save(any(Application.class))).thenReturn(getApplication());
        when(conveyorClient.getLoanOffers(getLoanApplicationRequestDTO())).thenReturn(expected);

        assertAll(
                () ->  assertEquals(loanOfferMapper.mapToListLoanOfferDTO(expected),
                        dealService.application(getLoanApplicationRequestDTO()))
        );
    }

    @Test
    void updateApplication() {
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(getApplication()));

        Application actual = dealService.updateApplication(getLoanOfferDTO());
        Application expected = getApplication();

        expected.setStatus(ApplicationStatus.APPROVED);
        expected.setAppliedOffer(loanOfferMapper.mapToLoanOffer(getLoanOfferDTO()));

        assertAll(
                () -> assertEquals(expected.getStatus(), actual.getStatus()),
                () -> assertEquals(expected.getAppliedOffer(), actual.getAppliedOffer())
        );
    }

    @Test
    void calculateCreditByApplicationId() {
        Optional<Application> application = Optional.of(getApplication());
        application.get().setAppliedOffer(loanOfferMapper.mapToLoanOffer(getLoanOfferDTO()));
        when(applicationRepository.findById(1L)).thenReturn(application);
        when(conveyorClient.getCalculation(any(ScoringDataDTO.class))).thenReturn(getCreditDTO());
        when(creditMapper.mapCredit(any(CreditDTO.class))).thenReturn(getCredit());

        CreditDTO actual = dealService.calculateCreditByApplicationId(1L, getFinishRegistrationRequestDTO());

        assertAll(
                () -> assertEquals(getCreditDTO().getAmount(), actual.getAmount()),
                () -> assertEquals(getCreditDTO().getRate(), actual.getRate()),
                () -> assertEquals(getCreditDTO().getTerm(), actual.getTerm())
        );
    }
}