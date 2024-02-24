# Meventer - 
> Приложение для тех, кто ценит своё время

## Содержание:
1. [Аргументы запросов](#requests_arguments):
    1) [Пользователь](#requests_arguments_user):
       1) [UserRegister](#requests_arguments_user_UserRegister);
       2) [UserLogin](#requests_arguments_user_UserLogin);
       3) [UserEmailCode](#requests_arguments_user_UserEmailCode).
2. [Результаты выполнения запросов](#requests_results):
   1) [Общие](#requests_results_general):
      1) [ResultResponse](#requests_results_general_ResultResponse);
      2) [Response](#requests_results_general_Response).

3. [Запросы](#requests):
   1) [Пользователь](#requests_user):
      1) [http://127.0.0.1:8080/user/register](#requests_user_register);
      2) [http://127.0.0.1:8080/user/login](#requests_user_login);
      3) [http://127.0.0.1:8080/user/sendEmailCode](#requests_user_send_email_code);
      4) [http://127.0.0.1:8080/user/verifyEmailCode](#requests_user_verify_email_code).
4. [Структура базы данных](#database_structure):
   1) [Users](#database_structure_users)
   2) [UsersEmailCodes](#database_structure_users_email_codes)

## Аргументы запросов <a name="requests_arguments"></a>
### Пользователь <a name="requests_arguments_user"></a>
+ UserRegister <a name="requests_arguments_user_UserRegister"></a>
```kotlin
UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String?,
    val dateOfBirth: String
)
```
```json
{
    "code": "000000",
    "email": "email@email.com",
    "password": "password",
    "nickname": "user",
    "avatar": null,
    "dateOfBirth": "0000-01-01"
}
```
+ UserLogin <a name="requests_arguments_user_UserLogin"></a>
```kotlin
UserLogin(
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
+ UserEmailCode <a name="requests_arguments_user_UserEmailCode"></a>
```kotlin
UserEmailCode(
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
## Результаты выполнения запросов <a name="requests_results"></a>
### Общие <a name="requests_results_general"></a>
+ ResultResponse <a name="requests_results_general_ResultResponse"></a>
```kotlin
ResultResponse(
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
+ Response <a name="requests_results_general_Response"></a>
```kotlin
Response<Type>(
    val result: ResultResponse,
    val data: Type? = null
)
```
```json
{
    "result": {
      "code": 200,
      "message": "OK"
    },
    "data": null
}
```
## Запросы <a name="requests"></a>
### Пользователь <a name="requests_user"></a>
+ http://127.0.0.1:8080/user/register <a name="requests_user_register"></a>
Аргументы: [UserRegister]()
<br> Результат выполнения: [Response< String >]()
<br> Описание: Регистрация нового пользователя, возвращающий токен аутентификации.
<br> `code = 200`: Регистрация успешна, в `data` передан `Bearer токен`.
<br> `code ≠ 200`: Регистрация неуспешна, в `data` передано `null`, в `message` указана причина. <br><br>
+ http://127.0.0.1:8080/user/login <a name="requests_user_login"></a>
Аргументы: [UserLogin]()
<br> Результат выполнения: [Response< String >]()
<br> Описание: Вход в систему зарегистрированного пользователя, возвращающий токен аутентификации.
<br> `code = 200`: Вход успешен, в `data` передан `Bearer токен`.
<br> `code ≠ 200`: Вход неуспешен, в `data` передано `null`, в `message` указана причина. <br><br>
+ http://127.0.0.1:8080/user/sendEmailCode <a name="requests_user_send_email_code"></a>
Аргументы: String
<br> Результат выполнения: [ResultResponse]()
<br> Описание: Отправляет код верификации на указанную почту. <br><br>
+ http://127.0.0.1:8080/user/verifyEmailCode <a name="requests_user_verify_email_code"></a>
Аргументы: [UserEmailCode]()
<br> Результат выполнения: [ResultResponse]()
<br> Описание: Проверяет вводимый код верификации почты и возврщает результат.
<br> `code = 200`: Код верификации правильный.
<br> `code ≠ 200`: Код верификации неправильный.
## Структура базы данных <a name="database_structure"></a>
+ Users <a name="database_structure_users"></a>
<br> id `integer`
<br> email `text`
<br> password `text`
<br> nickname `text`
<br> avatar `text`
<br> date_of_birth `date`
<br> rating `real`
<br> events `integer[]`
+ UsersEmailCodes <a name="database_structure_users_email_codes"></a>
<br> id `integer`
<br> email `text`
<br> code `varchar(6)`
<br> expiration_time `bigint`
