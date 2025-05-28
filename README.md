# микросервис account-mc

### по умолчанию запускается на http://localhost:8080/ (при необходимости поменять в docker-compose.yml)

### cd docker

### docker-compose up

## *Получение информации о текущем аккаунте*

[//]: # (operationId: getCurrentAccount)

### GET

## /api/v1/account/me

### RequestHeader

- Authorization: "JwtToken"

## *Обновление аккаунта*

operationId: updateAccountMe

### PUT

## /api/v1/account/me

### RequestHeader

- Authorization: "JwtToken"

### RequestBody

````json
{
"firstName": "",
"lastName": "",
"phone": "",
"photo": "",
"about": "",
"city": "",
"country": "",
"birthDate": "",
"emojiStatus": ""
}
````

## *Пометить текущий аккаунт как удалённый*

[//]: # (operationId: markAccountAsDeleted)

### DELETE

## /api/v1/account/me

### RequestHeader

- Authorization: "JwtToken"

## *Получение аккаунта по email*

[//]: # (operationId: getAccount)

### GET

## /api/v1/account

### RequestParam

- email: "String"

## *Создание нового аккаунта*

[//]: # (operationId: createAccount)

### POST

## /api/v1/account

### RequestBody

````json
{
"id": "",
"firstName": "",
"lastName": "",
"email": "",
"password": "",
"phone": "",
"photo": "",
"profileCover": "",
"about": "",
"city": "",
"country": "",
"statusCode": "FRIEND",
"regDate": "",
"birthDate": "",
"messagePermission": "",
"lastOnlineTime": "",
"emojiStatus": "",
"createdOn": "",
"updatedOn": "",
"deletionTimestamp": "",
"blocked": false,
"deleted": false,
"isOnline": true
}
````

## *Прием UUID от сервиса Dialogs через Webclient о завершении сессии вебсокета у аккаунта: как флаг перехода в статус
offline*

[//]: # (operationId: receiveUUIDFromPath)

### POST

## /api/v1/account/lastAction/{id}

### PathVariable

- id: "String" (UUID)

## *Получение аккаунта по ID*

[//]: # (operationId: getAccountById)

### GET

## /api/v1/account/{id}

### PathVariable

- id: "String" (UUID)

## *Пометить аккаунт как удалённый по ID*

[//]: # (operationId: markAccountAsDeletedById)

### DELETE

## /api/v1/account/{id}

### PathVariable

- id: "String" (UUID)

## *Пометить аккаунт как заблокированный по ID*

[//]: # (operationId: markAccountAsBlockedById)

### PATCH

## /api/v1/account/{id}

### PathVariable

- id: "String" (UUID)

## *Получение общего количества аккаунтов для telegram-бота*

[//]: # (operationId: getTotalAccountsCount)

### GET

## /api/v1/account/total

## *Глобальный поиск аккаунта по ключевым словам*

[//]: # (operationId: searchAccounts)

### GET

## /api/v1/account/search

### RequestBody

````json
{
"author": "",
"ids": [],
"firstName": "",
"lastName": "",
"ageTo": 0,
"ageFrom": 0,
"country": "",
"city": "",
"statusCode": "FRIEND",
"isDeleted": false
}
````

### RequestParam

- pageSize: 3
- pageNumber: 0

## *Поиск аккаунта по статус-коду отношений в микросервисе Friends. Этот контроллер ссылается на глобальный поиск
аккаунтов /search, так как в нем учтен statusCode.*

[//]: # (operationId: searchByStatusCode)

### GET

## /api/v1/account/search/statusCode

### RequestParam

- statusCode: FRIEND
- pageSize: 0
- pageNumber: 0
