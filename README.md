# Meventer

> Приложение для тех, кто ценит своё время

## Содержание

1. [Запросы:](#requests)
    + [Пользователь:](#requests_user)
        + [https://127.0.0.1:8080/user/register](#requests_user_register)
        + [https://127.0.0.1:8080/user/login](#requests_user_login)
        + [https://127.0.0.1:8080/user/sendEmailCode](#requests_user_sendEmailCode)
        + [https://127.0.0.1:8080/user/verifyEmailCode](#requests_user_verifyEmailCode)
        + [https://127.0.0.1:8080/user/data](#requests_user_data)
        + [https://127.0.0.1:8080/user/verifyToken](#requests_user_verifyToken)
        + [https://127.0.0.1:8080/user/feedback/create](#requests_user_create)
        + [https://127.0.0.1:8080/user/feedback/get](#requests_user_get)
    + [Мероприятие:](#requests_event)
        + [https://127.0.0.1:8080/event/create](#requests_event_create)
        + [https://127.0.0.1:8080/event/user](#requests_event_user)
        + [https://127.0.0.1:8080/event/update](#requests_event_update)
        + [https://127.0.0.1:8080/event/delete](#requests_event_delete)
        + [https://127.0.0.1:8080/event/changeUsers/participant](#requests_event_participant)
        + [https://127.0.0.1:8080/event/changeUsers/organizer](#requests_event_organizer)
        + [https://127.0.0.1:8080/event/changeUsers/featured](#requests_event_featured)
        + [https://127.0.0.1:8080/event/global](#requests_event_global)
        + [https://127.0.0.1:8080/event/{eventID}](#requests_event_id)
    + [Файл:](#requests_file)
        + [https://127.0.0.1:8080/file/{fileName}](#requests_file_name)
2. [Аргументы запросов:](#arguments)
    + [Пользователь:](#arguments_user)
        + [UserRegister](#arguments_user_register)
        + [UserLogin](#arguments_user_login)
        + [NullableUserID](#arguments_user_id)
        + [UserEmailCode](#arguments_user_emailCode)
        + [UserFeedbackCreate](#arguments_user_feedback_create)
    + [Мероприятие:](#arguments_event)
        + [EventCreate](#arguments_event_create)
        + [EventSelection](#arguments_event_selection)
        + [EventOrganizer](#arguments_event_organizer)
        + [EventsGet](#arguments_event_get)
        + [EventUpdate](#arguments_event_update)
3. [Результаты выполнения запросов:](#results)
    + [Общие:](#results_general)
        + [ResultResponse](#results_general_result)
        + [Response](#results_general_response)
    + [Пользователь:](#results_user)
        + [User](#results_user_user)
        + [UserFeedback](#results_user_feedback)
    + [Мероприятие:](#results_event)
        + [Event](#results_event_event)
4. [Структура базы данных:](#database)
    + [Users](#database_users)
    + [UsersEmailCodes](#database_usersEmailCodes)
    + [UsersFeedback](#database_usersFeedback)
    + [Events](#database_events)

## Запросы <a name="requests"></a>

### Пользователь: <a name="requests_user"></a>

+ Регистрация (https://127.0.0.1:8080/user/register): <a name="requests_user_register"></a> \
  Метод: `POST` \
  Аргументы: `MultiPartData` - `UserRegister`, `image` \
  Заголовки запроса: Content-Type - `application/json` \
  Результат выполнения: `Response < String >` \
  Описание: Регистрация нового пользователя, возвращает токен аутентификации. \
  `code = 200`: Регистрация успешна, в `data` передан `Bearer токен`. \
  `code ≠ 200`: Регистрация неуспешна, в `data` передан `null`.
+ Вход (https://127.0.0.1:8080/user/login): <a name="requests_user_login"></a> \
  Метод: `POST` \
  Аргументы: `UserLogin` \
  Заголовки запроса: Content-Type - `application/json` \
  Результат выполнения: `Response < String >` \
  Описание: Вход зарегистрированного пользователя, возвращает токен аутентификации. \
  `code = 200`: Вход успешен, в `data` передан `Bearer токен`. \
  `code ≠ 200`: Вход неуспешен, в `data` передан `null`.
+ Отправка кода подтверждения на почту(https://127.0.0.1:8080/user/sendEmailCode): <a name="requests_user_sendEmailCode"></a> \
  Метод: `POST` \
  Аргументы: `String`
  Заголовки запроса: Content-Type - `application/json` \
  Результат выполнения: `ResultResponse` \
  Описание: Отправляет на указанную почту код подтверждения, если в процессе выполнения возникает проблема с почтой,
  то код верификации не приходит на почту. \
  `code = 200`: Отправка кода начата. \
  `code ≠ 200`: Вероятна ошибка в запросе.
+ Верификация кода подтверждения (https://127.0.0.1:8080/user/verifyEmailCode): <a name="requests_user_verifyEmailCode"></a> \
  Метод: `POST` \
  Аргументы: `UserEmailCode` \
  Заголовки запроса: Content-Type - `application/json` \
  Результат выполнения: `ResultResponse` \
  Описание: Проверяет правильность кода подтверждения с почты. \
  `code = 200`: Код верный. \
  `code ≠ 200`: Код неверный.
+ Получение данных пользователя (https://127.0.0.1:8080/user/data): <a name="requests_user_data"></a> \
  Метод: `POST` \
  Аргументы: `NullableUserID` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `Response < User >` \
  Описание: Если ID пользователя указан в аргументе, то возвращает информацию о нём, иначе возвращает
  информацию о владельце токена аутентификации. \
  `code = 200`: Пользователь найден, информация о нём передана в `data`. \
  `code ≠ 200`: Пользователь не найден или токен аутентификации недействителен
  (в таком случае результатом является `ResultResponse`), в `data` передан `null`
+ Верификация токена аутентикации (https://127.0.0.1:8080/user/verifyToken): <a name="requests_user_verifyToken"></a> \
  Метод: `GET` \
  Аргументы: -
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Проверяет валидность токена аутентификации. \
  `code = 200`: Токен действительный. \
  `code ≠ 200`: Токен недействительный.
+ Создание отзыва на пользователя (https://127.0.0.1:8080/user/feedback/create): <a name="requests_user_create"></a> \
  Метод: `POST` \
  Аргументы: `UserFeedbackCreate`
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Создаёт отзыв на пользователя. \
  `code = 200`: Пользователи, от кого и кому, найдены, создан отзыв. \
  `code ≠ 200`: Как минимум, один из пользователей не найден, либо пользователь пытался отправить отзыв на себя,
  либо отзыв от пользователя на этого пользователя уже был создан до этого, либо токен аутентификации недействителен.
+ Получение отзывов на пользователя (https://127.0.0.1:8080/user/feedback/get): <a name="requests_user_get"></a> \
  Метод: `POST` \
  Аргументы: `NullableUserID` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `Response < List < UserFeedback > >` \
  Описание: Получение всех отзывов на указанного пользователя. \
  `code = 200`: Отзывы на пользователя найдены, в `data` переданы все отзывы. \
  `code ≠ 200`: Отзывы на пользователя не найдены, либо токен аутентификации недействителен.

### Мероприятие: <a name="requests_event"></a>

+ Создание мероприятия (https://127.0.0.1:8080/event/create): <a name="requests_event_create"></a> \
  Метод: `POST` \
  Аргументы: `MultiPartData` - `EventCreate`, `images` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Создаёт мероприятие. \
  `code = 200`: Мероприятие создано. \
  `code ≠ 200`: Мероприятие не создано: не отправлена информация о мероприятии, либо токен аутентификации недействителен
+ Получение мероприятий пользователя (https://127.0.0.1:8080/event/user): <a name="requests_event_user"></a> \
  Метод: `POST` \
  Аргументы: `EventsGet`
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `Response < List < Event > >` \
  Описание: Получение всех мероприятий пользователя; в `type` указывается тип получения:
    + `null` или `пустая строка`: получение **абсолютно** всех мероприятий пользователя;
    + `participant`: получение всех мероприятий пользователя, где он является **участником**;
    + `organizer`: получение всех мероприятий пользователя, где он является **организатором**;
    + `featured`: получение всех мероприятий пользователя, которые находятся в **избранном**;
    + `originator`: получение всех мероприятий пользователя, где является **создателем**.

  `code = 200`: Найдены мероприятия пользователя и переданы в `data`. \
  `code = 204`: Не найдены мероприятие пользователя, в `data` передан `null`. \
  `code ≠ 200`: Токен аутентификации недействителен (`ResultResponse`).
+ Изменение мероприятия (https://127.0.0.1:8080/event/update): <a name="requests_event_update"></a> \
  Метод: `POST` \
  Аргументы: `EventUpdate`
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse`
  Описание: Изменение мероприятия.
  `code = 200`: Найдено и изменено мероприятие.
  `code ≠ 200`: Не найдено мероприятие,
+ Удаление мероприятия (https://127.0.0.1:8080/event/delete): <a name="requests_event_delete"></a> \
  Метод: `POST` \
  Аргументы: `Int` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Удаление мероприятия, в аргументы передаётся `ID мероприятия`.
  `code = 200`: Найдено и удалено мероприятие.
  `code ≠ 200`: Мероприятие не найдено, либо токен аутентификации недействителен.
+ Изменение статуса пользователя как участника (https://127.0.0.1:8080/event/changeUsers/participant): 
<a name="requests_event_participant"></a> \
  Метод: `POST` \
  Аргументы: `Int` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Меняет пользователя на участника, либо убирает пользователя из списка участников.
  `code = 200`: Пользователь и мероприятие найдены, его статус изменён.
  `code ≠ 200`: Пользователь не найден, либо мероприятие не найдено, либо пользователь является создателем мероприятия,
  либо токен аутентификации недействителен.
+ Изменение статуса пользователя как организатора (https://127.0.0.1:8080/event/changeUsers/organizer):
  <a name="requests_event_organizer"></a> \
  Метод: `POST` \
  Аргументы: `EventOrganizer` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Меняет пользователя на организатора, либо убирает пользователя из списка участников. \
  `code = 200`: Пользователь и мероприятие найдены, его статус изменён. \
  `code ≠ 200`: Пользователь не найден, либо мероприятие не найдено, либо пользователь является создателем мероприятия,
  либо токен аутентификации недействителен.
+ Добавление/удаление мероприятия из избранного для пользователя (https://127.0.0.1:8080/event/changeUsers/featured):
  <a name="requests_event_featured"></a> \
  Метод: `POST` \
  Аргументы: `Int` \
  Заголовки запроса: Content-Type - `application/json`; Authentication - `Bearer {token}` \
  Результат выполнения: `ResultResponse` \
  Описание: Добавляет/удаляет мероприятие из избранного для пользователя;
  в аргументах запроса указывается `ID мероприятия`. \
  `code = 200`: Мероприятие найдено, и изменён его статус для пользователя. \
  `code ≠ 200`: Мероприятие или пользователь не найдены, либо токен аутентификации не найден.
+ Получение глобальных мероприятий с возможностью выборки (https://127.0.0.1:8080/event/global):
  <a name="requests_event_global"></a> \
  Метод: `POST` \
  Аргументы: `EventSelection` \
  Заголовки запроса: Content-Type - `application/json` \
  Результат выполнения: `Response < List < Event > >` \
  Описание: Получение глобальных мероприятий, выбранных по предоставленным ключевым данным;
  в `sortBy` указан порядок, по которому отсортированы мероприятия:
    + `Nearest ones first`: ближайшие мероприятия сначала;
    + `Further ones first`: дальнейшие мероприятия сначала. \

  `code = 200`: Мероприятия найдены, и информация о них передана в `data`. \
  `code ≠ 200`: Ошибка в запросе.
+ Получение данных об одном мероприятии (https://127.0.0.1:8080/event/{eventID}): <a name="requests_event_id"></a> \
  Метод: `GET` \
  Аргументы: - \
  Заголовки запроса: - \
  Результат выполнения: `Response < Event >` \
  Описание: Получение мероприятия по его `ID`, который указывается в параметре запроса. \
  `code = 200`: Мероприятие найдено, и информация о нём передана в `data` \
  `code ≠ 200`: Мероприятие не найдено, в `data` передано `null`.

### Файл: <a name="requests_file"></a>

+ Получение файла по названию (https://127.0.0.1:8080/file/{fileName}): <a name="requests_file_name"></a> \
  Метод: `GET` \
  Аргументы: - \
  Заголовки запроса: - \
  Результат выполнения: `File` \
  Описание: Получение файла по `названию`, которое указано в параметре. \
  `code = 200`: Файл найден и передан в ответе. \
  `code ≠ 200`: Файл не найден.

## Аргументы запросов <a name="arguments"></a>

### Пользователь: <a name="arguments_user"></a>

+ UserRegister <a name="arguments_user_register"></a>

```kotlin
class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String?,
    val name: String,
    val dateOfBirth: LocalDate,
)
```

```json
{
  "code": "000000",
  "email": "email@email.com",
  "password": "password",
  "nickname": null,
  "name": "name",
  "dateOfBirth": "2000-01-01"
}
```

+ UserLogin <a name="arguments_user_login"></a>

```kotlin
class UserLogin(
    val email: String,
    val password: String
)
```

```json
{
  "email": "email@email.com",
  "password": "password"
}
```

+ NullableUserID <a name="arguments_user_id"></a>

```kotlin
class NullableUserID(
    val id: Int?
)
```

```json
1
```

+ UserEmailCode <a name="arguments_user_emailCode"></a>

```kotlin
class UserEmailCode(
    val email: String,
    val code: String
)
```

```json
{
  "email": "email@email.com",
  "code": "000000"
}
```

+ UserFeedbackCreate <a name="arguments_user_feedback_create"></a>

```kotlin
class UserFeedbackCreate(
    val toUserID: Int,
    val rating: Float,
    val comment: String
)
```

```json
{
  "toUserID": 1,
  "rating": 5.0,
  "comment": "Comment!"
}
```

### Мероприятие: <a name="arguments_event"></a>

+ EventCreate <a name="arguments_event_create"></a>

```kotlin
class EventCreate(
    val name: String,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?
)
```

```json
{
  "name": "Event",
  "description": "Event description",
  "startTime": "2024-03-11T10:00:48.315738500Z",
  "minimalAge": 0,
  "maximalAge": null,
  "price": 0
}
```

+ EventSelection <a name="arguments_event_selection"></a>

```kotlin
class EventSelection(
    val tags: List<String>?,
    val age: Short?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String?
)
```

```json
{
  "tags": [
    "tag1",
    "tag2"
  ],
  "age": 12,
  "minimalPrice": 20,
  "maximalPrice": null,
  "sortBy": null
}
```

+ EventOrganizer <a name="arguments_event_organizer"></a>

```kotlin
class EventOrganizer(
    val eventID: Int,
    val changingID: Int
)
```

```json
{
  "eventID": 10,
  "changingID": 20
}
```

+ EventsGet <a name=arguments_event_get""></a>

```kotlin
class EventsGet(
    val userID: Int?,
    val actual: Boolean?,
    val aforetime: Boolean?,
    val type: String?
)
```

```json
{
  "userID": 21,
  "actual": true,
  "aforetime": null,
  "type": null
}
```

+ EventUpdate <a name="arguments_event_update"></a>

```kotlin
class EventUpdate(
    val eventID: Int,
    val name: String?,
    val description: String?,
    val startTime: Instant?,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?
)
```

```json
{
  "eventID": 12,
  "name": "Event upd",
  "description": "Event description upd",
  "startTime": "2024-03-11T10:00:48.315738500Z",
  "minimalAge": 12,
  "maximalAge": 19,
  "price": 100
}
```

## Результаты выполнения запросов <a name="results"></a>

### Общие: <a name="results_general"></a>

+ ResultResponse <a name="results_general_result"></a>

```kotlin
class ResultResponse(
    val code: UShort,
    val message: String
)
```

```json
{
  "code": 200,
  "message": "OK"
}
```

+ Response <a name="results_general_response"></a>
```kotlin
class Response<Type>(
    val result: ResultResponse,
    val data: Type? = null
)
```

```json
{
  "result": {
    "code": 404,
    "message": "Not found"
  },
  "data": null
}
```

### Пользователь: <a name="results_user"></a>

+ User <a name="results_user_user"></a>
```kotlin
class User(
    val id: Int,
    val email: String,
    val avatar: String,
    val dateOfBirth: LocalDate
)
```

```json
{
  "id": 10,
  "email": "email@email.com",
  "avatar": "avatar.png",
  "dateOfBirth": "2000-01-01"
}
```
+ UserFeedback <a name="results_user_feedback"></a>

```kotlin
class UserFeedback(
    val fromUserID: Int,
    val rating: Float,
    val comment: String
)
```

```json
{
  "fromUserID": 1,
  "rating": 4.5,
  "comment": "OK..."
}
```

### Мероприятие: <a name="results_event"></a>

+ Event <a name="results_event_event"></a>
```kotlin
class Event(
    val id: Int,
    val name: String,
    val images: List<String>,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short,
    val maximalAge: Short?,
    val price: Int,
    val originator: Int,
    val organizers: List<Int>
)
```

```json
{
  "id": 120,
  "name": "Event",
  "images": "image1.jpg, image2.jpeg",
  "description": "Event description",
  "startTime": "2024-03-11T10:00:48.315738500Z",
  "minimalAge": 0,
  "maximalAge": null,
  "price": 120,
  "originator": 12,
  "organizers": []
}
```

## Структура базы данных <a name="database"></a>

### Users <a name="database_users"></a>
+ id `integer` `pk`
+ email `text`
+ password `text`
+ nickname `text`
+ name `text`
+ avatar `text`
+ date_of_birth `date`

### UsersEmailCodes <a name="database_usersEmailCodes"></a>
+ id `integer` `pk`
+ email `text`
+ code `varchar(6)`
+ expiration_time `bigint`

### UsersFeedback <a name="database_usersFeedback"></a>
+ id `bigint` `pk` 
+ to_user_id `integer`
+ from_user_id `integer`
+ rating `real`
+ comment `text`
### Events <a name="database_events"></a>
+ id `integer` `pk`
+ name `text`
+ images `text[]`
+ description `text`
+ start_time `timestamp without time zone`
+ minimal_age `smallint`
+ maximal_age `smallint`
+ price `integer`
+ originator `integer`
+ participants `integer[]`
+ organizers `integer[]`
+ in_favourites `integer[]`