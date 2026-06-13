# Лабораторная работа №5 — Spring Boot + Spring Security + JWT

Проект является продолжением лабораторной №4: система уведомлений на Spring Boot, PostgreSQL, Spring Data JPA и Bean Validation. В лабораторной №5 добавлена безопасность: регистрация пользователей, хранение паролей в виде BCrypt-хэша, роли, HTTP Basic, JWT и ограничение доступа к URL/методам.

## Стек

- Java 17;
- Spring Boot 4.0.3;
- Spring Web;
- Spring Data JPA;
- Spring Security;
- PostgreSQL;
- Hibernate;
- JJWT 0.12.6;
- Bean Validation;
- Lombok.

## Что реализовано

- подключен `spring-boot-starter-security`;
- добавлены зависимости `jjwt-api`, `jjwt-impl`, `jjwt-jackson`;
- сущность `User` расширена полями `password` и `role`;
- добавлено перечисление `UserRole`: `ROLE_USER`, `ROLE_ADMIN`;
- добавлены DTO `RegisterRequest`, `LoginRequest`, `AuthResponse`;
- добавлен `CustomUserDetails`;
- добавлен `CustomUserDetailsService`;
- настроены `PasswordEncoder`, `DaoAuthenticationProvider`, `AuthenticationManager`, `SecurityFilterChain`;
- реализована регистрация обычного пользователя `/auth/register`;
- реализована регистрация администратора `/auth/register-admin`;
- реализован логин `/auth/login` с выдачей JWT;
- реализован `JwtService` с генерацией, чтением username и проверкой срока действия токена;
- реализован `JwtAuthenticationFilter`;
- включена stateless-настройка `SessionCreationPolicy.STATELESS`;
- включен HTTP Basic для проверки через Postman;
- добавлен admin endpoint `/admin/ping`;
- удаление пользователя защищено через `@PreAuthorize("hasRole('ADMIN')")`;
- добавлена обработка ошибок неверного логина/пароля и недостатка прав.

## Структура проекта

```text
org.example
├── config
│   └── SecurityConfig
├── controller
│   ├── AdminController
│   ├── AuthController
│   ├── HelloController
│   ├── NotificationController
│   └── UserController
├── exception
├── mapper
├── model
│   ├── dto
│   │   ├── AuthResponse
│   │   ├── LoginRequest
│   │   ├── RegisterRequest
│   │   ├── NotificationDto
│   │   └── UserDto
│   ├── entity
│   │   ├── Notification
│   │   └── User
│   └── enums
│       ├── NotificationChannel
│       ├── NotificationStatus
│       └── UserRole
├── repository
├── security
│   ├── CustomUserDetails
│   ├── CustomUserDetailsService
│   ├── JwtAuthenticationFilter
│   └── JwtService
└── service
    ├── AuthService
    ├── NotificationService
    └── UserService
```

## Подготовка PostgreSQL

Создай базу данных:

```sql
CREATE DATABASE spring_lab4_notifications;
```

Параметры подключения задаются в `src/main/resources/application.properties` через переменные окружения:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/spring_lab4_notifications}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

Можно создать файл `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/spring_lab4_notifications
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

Если база уже содержит таблицу `users` из лабораторной №4, при первом запуске после добавления `password` и `role` может потребоваться очистить старые данные, потому что новые поля обязательные:

```sql
TRUNCATE TABLE notifications RESTART IDENTITY CASCADE;
TRUNCATE TABLE users RESTART IDENTITY CASCADE;
```

## Запуск

### Через IntelliJ IDEA

1. Открой проект.
2. Дождись загрузки Maven.
3. Проверь настройки PostgreSQL.
4. Запусти класс `org.example.Application`.

### Через Maven

```bash
./mvnw spring-boot:run
```

В архиве исходного проекта wrapper-файл назывался `mvnm`. Если у тебя он остался с таким именем, можно либо переименовать его в `mvnw`, либо запускать через установленный Maven:

```bash
mvn spring-boot:run
```

## Правила доступа

| URL | Доступ |
|---|---|
| `/hello` | открыт всем |
| `/auth/**` | открыт всем |
| `/users/**` | `ROLE_USER` или `ROLE_ADMIN` |
| `/notifications/**` | `ROLE_USER` или `ROLE_ADMIN` |
| `/admin/**` | только `ROLE_ADMIN` |
| остальные URL | нужен вход |

Важно: в базе роль хранится как `ROLE_ADMIN`, но в конфигурации используется `hasRole("ADMIN")`, потому что Spring Security автоматически добавляет префикс `ROLE_`.

## Проверка через Postman

### 1. Регистрация пользователя

**POST** `http://localhost:8080/auth/register`

```json
{
  "name": "Иван Иванов",
  "email": "ivan@example.com",
  "password": "qwerty123"
}
```

Ожидаемый ответ:

```text
Пользователь успешно зарегистрирован
```

В базе данных проверь:

```sql
SELECT id, name, email, password, role, created_at FROM users;
```

Пароль должен быть похож на BCrypt-хэш, например начинаться с `$2a$`, `$2b$` или `$2y$`, а роль должна быть `ROLE_USER`.

### 2. Регистрация администратора

**POST** `http://localhost:8080/auth/register-admin`

```json
{
  "name": "Администратор",
  "email": "admin@example.com",
  "password": "admin123"
}
```

Ожидаемый результат: в таблице `users` появится пользователь с ролью `ROLE_ADMIN`.

### 3. Проверка закрытого URL без авторизации

**GET** `http://localhost:8080/users/all`

Ожидаемый результат: `401 Unauthorized`.

### 4. Проверка HTTP Basic

В Postman открой вкладку **Authorization**:

- Type: `Basic Auth`;
- Username: `ivan@example.com`;
- Password: `qwerty123`.

После этого выполни:

```text
GET http://localhost:8080/users/all
GET http://localhost:8080/notifications/all
```

Ожидаемый результат: доступ разрешен.

### 5. Логин и получение JWT

**POST** `http://localhost:8080/auth/login`

```json
{
  "email": "ivan@example.com",
  "password": "qwerty123"
}
```

Ожидаемый ответ:

```json
{
  "token": "...",
  "tokenType": "Bearer"
}
```

### 6. Запрос с JWT

Скопируй `token` и добавь заголовок:

```text
Authorization: Bearer <твой_токен>
```

Проверь:

```text
GET http://localhost:8080/users/all
GET http://localhost:8080/notifications/all
```

Ожидаемый результат: `200 OK`.

### 7. Проверка ролей

Обычный пользователь:

```text
GET http://localhost:8080/admin/ping
Authorization: Bearer <токен_ROLE_USER>
```

Ожидаемый результат: `403 Forbidden`.

Администратор:

```text
GET http://localhost:8080/admin/ping
Authorization: Bearer <токен_ROLE_ADMIN>
```

Ожидаемый результат:

```text
Только для администратора
```

### 8. Проверка `@PreAuthorize`

Удаление пользователя защищено на уровне метода сервиса:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id)
```

Проверка:

```text
DELETE http://localhost:8080/users/1
Authorization: Bearer <токен_ROLE_USER>
```

Ожидаемый результат: `403 Forbidden`.

```text
DELETE http://localhost:8080/users/1
Authorization: Bearer <токен_ROLE_ADMIN>
```

Ожидаемый результат: пользователь удален.

## Готовый файл запросов

Для быстрой проверки добавлен файл:

```text
lab5_requests.http
```

Его можно открыть в IntelliJ IDEA и запускать HTTP-запросы прямо из редактора.

## Краткая теория

### Аутентификация

Аутентификация отвечает на вопрос: кто выполняет запрос? Например, пользователь отправляет email и пароль, а приложение проверяет, существует ли такой пользователь и совпадает ли пароль.

### Авторизация

Авторизация отвечает на вопрос: что пользователю разрешено делать? Например, обычный пользователь может смотреть уведомления, но не может заходить на `/admin/ping`.

### SecurityFilterChain

`SecurityFilterChain` описывает правила безопасности приложения. В нем задается, какие URL открыты, какие требуют входа, какие доступны только по ролям, включен ли HTTP Basic, используется ли JWT-фильтр и как сервер работает с сессиями.

### UserDetailsService

`UserDetailsService` загружает пользователя по логину. В этом проекте логином является email. Сервис не проверяет пароль сам, он только возвращает пользователя для Spring Security.

### CustomUserDetails

`CustomUserDetails` — адаптер между сущностью `User` и интерфейсом `UserDetails`. Он показывает Spring Security, где взять email, пароль и роли пользователя.

### PasswordEncoder

`PasswordEncoder` нужен для безопасного хранения паролей. В проекте используется `BCryptPasswordEncoder`: при регистрации пароль превращается в хэш, а при входе введенный пароль сравнивается с сохраненным хэшем.

### AuthenticationManager

`AuthenticationManager` запускает процесс аутентификации. В `/auth/login` он получает email и пароль, передает их провайдеру и возвращает успешную или неуспешную аутентификацию.

### DaoAuthenticationProvider

`DaoAuthenticationProvider` использует `UserDetailsService` для загрузки пользователя и `PasswordEncoder` для проверки пароля.

### HTTP Basic

HTTP Basic передает логин и пароль в заголовке `Authorization: Basic ...`. Это удобно для учебной проверки через Postman, но в реальных REST API чаще используют токены.

### SecurityContext

`SecurityContext` хранит информацию о текущем аутентифицированном пользователе во время обработки запроса.

### Сессии

При сессионном подходе сервер хранит состояние пользователя. После входа создается сессия, а клиент получает cookie `JSESSIONID`. В следующих запросах cookie позволяет восстановить пользователя.

### JWT

JWT — stateless-подход. Сервер выдает токен после логина, клиент сохраняет его и отправляет при каждом запросе в заголовке `Authorization: Bearer ...`. Сервер проверяет подпись и срок действия токена, после чего восстанавливает пользователя для текущего запроса.

### SessionCreationPolicy.IF_REQUIRED и STATELESS

`IF_REQUIRED` разрешает Spring Security создавать сессию при необходимости. `STATELESS` запрещает хранить состояние пользователя на сервере, поэтому каждый запрос должен нести данные авторизации, например JWT или Basic Auth.

## Ответы на контрольные вопросы

1. **Что такое аутентификация и чем она отличается от авторизации?**  
   Аутентификация проверяет личность пользователя, авторизация проверяет его права.

2. **Что делает SecurityFilterChain?**  
   Определяет цепочку security-фильтров и правила доступа к URL.

3. **Зачем нужен UserDetailsService?**  
   Чтобы загружать пользователя из базы данных по логину.

4. **Для чего используется PasswordEncoder?**  
   Для хэширования пароля и проверки введенного пароля с сохраненным хэшем.

5. **Почему пароли нельзя хранить в открытом виде?**  
   При утечке базы злоумышленник сразу получит реальные пароли пользователей.

6. **Какую роль выполняет AuthenticationManager?**  
   Запускает процесс проверки учетных данных пользователя.

7. **Что делает DaoAuthenticationProvider?**  
   Загружает пользователя через `UserDetailsService` и проверяет пароль через `PasswordEncoder`.

8. **Как работает HTTP Basic?**  
   Клиент отправляет логин и пароль в заголовке `Authorization`, сервер проверяет их на каждом запросе.

9. **Что хранится в SecurityContext?**  
   Объект `Authentication` с информацией о пользователе и его ролях.

10. **Как работает аутентификация через сессию?**  
    После входа сервер сохраняет пользователя в сессии, а клиент получает cookie `JSESSIONID`.

11. **Что такое JWT и чем он отличается от сессии?**  
    JWT — подписанный токен, который хранится на клиенте. При JWT сервер не хранит состояние пользователя между запросами.

12. **Зачем нужен JWT-фильтр?**  
    Чтобы прочитать Bearer-токен из заголовка, проверить его и поместить пользователя в `SecurityContext`.

13. **Что делает addFilterBefore() в конфигурации?**  
    Добавляет пользовательский фильтр перед стандартным фильтром Spring Security.

14. **В чем разница между SessionCreationPolicy.IF_REQUIRED и STATELESS?**  
    `IF_REQUIRED` разрешает создавать сессию, `STATELESS` запрещает серверное хранение состояния.

15. **Как проверить доступ к URL по роли пользователя?**  
    Зарегистрировать пользователей с разными ролями, получить токены и выполнить запросы к защищенным URL, например `/admin/ping`.
