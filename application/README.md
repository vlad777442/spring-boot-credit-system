# Application Service

## Проект кредитный конвеер для neoflex.


## API endpoints:

### POST

* #### /application
1. По API приходит LoanApplicationRequestDTO
2. На основе LoanApplicationRequestDTO происходит прескоринг.
3. Отправляется POST-запрос на /deal/application в МС deal через FeignClient.
4. Ответ на API - список из 4х LoanOfferDTO от "худшего" к "лучшему".

### PUT
* #### /application/offer

1. По API приходит LoanOfferDTO
2. Отправляется POST-запрос на /deal/offer в МС deal через FeignClient.
