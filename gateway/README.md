# API-Gateway

## API endpoints:

### POST

* #### /gateway

Получение списка 4 возможных займов.

* #### /gateway/{applicationId}/send

Запрос на отправку документов.

* #### /gateway/{applicationId}/sign

Запрос на подписание документов.

* #### /gateway/{applicationId}/code

Подписание документов по SES коду.


### PUT
* #### /gateway/offer

1. По API приходит LoanOfferDTO
2. Достаётся из БД заявка(Application) по applicationId из LoanOfferDTO.
3. В заявке обновляется статус, история статусов(List<ApplicationStatusHistoryDTO>), принятое предложение LoanOfferDTO устанавливается в поле appliedOffer.
4. Заявка сохраняется.

* #### /gateway/calculate/{applicationId}

1. По API приходит объект FinishRegistrationRequestDTO и параметр applicationId (Long).
2. Достаётся из БД заявка(Application) по applicationId.
3. ScoringDataDTO насыщается информацией из FinishRegistrationRequestDTO и Client, который хранится в Application
4. Отправляется POST запрос на /conveyor/calculation МС conveyor с телом ScoringDataDTO через FeignClient.
5. На основе полученного из кредитного конвейера CreditDTO создаётся сущность Credit и сохраняется в базу со статусом CALCULATED.
6. В заявке обновляется статус, история статусов.
7. Заявка сохраняется.