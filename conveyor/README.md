# Credit Conveyor

## Проект кредитный конвеер для neoflex.

### Правила прескоринга:

Используется расчёт процентов по займу

Сумма процентов по займу = Сумма займа * Процентная ставка / 365 дней * Количество дней пользования займом;

### Правила скоринга.

#### Расчет аннуитентного платежа:

![Starting](/pictures/payment.jpg)

Коэффициент аннуитета:

![Starting](/pictures/coeff.jpg)


## API endpoints:

### POST

* #### /conveyor/offers

По API приходит LoanApplicationRequestDTO.

На основании LoanApplicationRequestDTO происходит прескоринг создаётся 4 кредитных предложения LoanOfferDTO на основании всех возможных комбинаций булевских полей isInsuranceEnabled и isSalaryClient
(false-false, false-true, true-false, true-true).

Ответ на API - список из 4х LoanOfferDTO от "худшего" к "лучшему" (чем меньше итоговая ставка, тем лучше).

* #### /conveyor/calculation

По API приходит ScoringDataDTO.

Происходит скоринг данных, высчитывание ставки(rate), полной стоимости кредита(psk), размер ежемесячного платежа(monthlyPayment), график ежемесячных платежей (List<PaymentScheduleElement>).

Ответ на API - CreditDTO, насыщенный всеми рассчитанными параметрами.


