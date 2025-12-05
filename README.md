# account-mc — сервис управления аккаунтами

Микросервис отвечает за создание, обновление, поиск и управление статусами пользовательских аккаунтов.  
По умолчанию сервис запускается на порту **8080**.

---

```bash
🚀 Запуск через Docker

cd docker
docker-compose up
После запуска сервис будет доступен по адресу:
http://localhost:8080/

📘 API
Большинство эндпоинтов требуют передачу JWT-токена:
Authorization: "JwtToken"

👤 Работа с текущим аккаунтом
▶ Получить данные своего аккаунта
GET /api/v1/account/me
Headers: Authorization

▶ Обновить свой аккаунт
PUT /api/v1/account/me
Headers: Authorization
Body:

json
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

▶ Пометить свой аккаунт как удалённый
DELETE /api/v1/account/me
Headers: Authorization

🔎 Получение аккаунтов
▶ Найти аккаунт по email
GET /api/v1/account
Params: email=string

▶ Получить аккаунт по ID
GET /api/v1/account/{id}
Path: id = UUID

➕ Создание аккаунта
▶ Создать новый аккаунт
POST /api/v1/account
Body:

json
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

🔄 Управление статусами аккаунта
▶ Пометить аккаунт как удалённый по ID
DELETE /api/v1/account/{id}

▶ Заблокировать аккаунт
PATCH /api/v1/account/{id}

▶ Установить статус "offline" (уведомление от Dialogs)
POST /api/v1/account/lastAction/{id}
Path: id = UUID

📊 Статистика
▶ Получить общее количество аккаунтов (для Telegram-бота)
GET /api/v1/account/total

🔍 Поиск
▶ Глобальный поиск аккаунтов
GET /api/v1/account/search
Body:

json
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
Params:

pageSize (пример: 3)

pageNumber (пример: 0)

▶ Поиск по статус-коду отношений
GET /api/v1/account/search/statusCode
Params:

statusCode=FRIEND

pageSize=0

pageNumber=0

Контроллер использует глобальный поиск (/search), расширяя его обработкой statusCode.

🧩 Примечания
Сервис реализует интеграции через WebClient.

Статусы онлайн/офлайн синхронизируются с микросервисом Dialogs.

Поддерживаются мягкое удаление и блокировка аккаунтов.
