# Что изменено для лабораторной №5

## Зависимости

В `pom.xml` добавлены:

- `spring-boot-starter-security`;
- `jjwt-api`;
- `jjwt-impl`;
- `jjwt-jackson`.

## Модель пользователя

В `User` добавлены поля:

- `password` — BCrypt-хэш пароля;
- `role` — роль пользователя.

Создан enum `UserRole`:

- `ROLE_USER`;
- `ROLE_ADMIN`.

## DTO

Созданы:

- `RegisterRequest`;
- `LoginRequest`;
- `AuthResponse`.

В `UserDto` добавлено поле `role`, чтобы при просмотре пользователей было видно их роль.

## Security

Созданы:

- `SecurityConfig`;
- `CustomUserDetails`;
- `CustomUserDetailsService`;
- `JwtService`;
- `JwtAuthenticationFilter`.

Настроены:

- `PasswordEncoder` через BCrypt;
- `DaoAuthenticationProvider`;
- `AuthenticationManager`;
- `SecurityFilterChain`;
- `SessionCreationPolicy.STATELESS`;
- HTTP Basic;
- JWT Bearer-токены;
- ограничение `/admin/**` только для `ROLE_ADMIN`.

## Auth API

Добавлены endpoint-ы:

- `POST /auth/register` — регистрация обычного пользователя;
- `POST /auth/register-admin` — регистрация администратора;
- `POST /auth/login` — логин и получение JWT.

## Роли и методная авторизация

Добавлен `AdminController`:

- `GET /admin/ping`.

Метод удаления пользователя защищен:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id)
```

## Проверка

Добавлен файл `lab5_requests.http` с готовыми запросами для IntelliJ IDEA.

Подробная инструкция и теория находятся в `README.md`.
