# OTP-Project

## Описание проекта

Проект представляет собой backend-приложение на языке Java, предназначенное для защиты операций с помощью одноразовых OTP-кодов (One Time Password).  
Основные функции:

- Регистрация пользователей и администраторов  
- Генерация OTP-кодов для операций  
- Отправка OTP-кодов через email или сохранение в файл  
- Проверка корректности OTP-кодов  
- Управление конфигурацией OTP-кодов (длина кода и время жизни)  
- Управление пользователями (просмотр и удаление)  

Проект построен на чистой архитектуре с использованием слоев API, Service и DAO.  
Подключение к базе данных осуществляется через PostgreSQL и JDBC.

---

## Требования

- Java 17+  
- Maven  
- PostgreSQL 17

---

## Инструкция по запуску

1. Клонируйте репозиторий:

```bash
git clone https://github.com/your_username/OTP-Project.git
cd OTP-Project
```

2. Создайте базу данных:

```sql
CREATE DATABASE otpdb;
```

3. Примените скрипт `schema.sql` для создания таблиц:

```bash
psql -U your_postgres_user -d otpdb -f schema.sql
```

4. Укажите настройки подключения к базе данных в `DatabaseConnection.java`:

```java
private static final String URL = "jdbc:postgresql://localhost:5432/otpdb";
private static final String USERNAME = "postgres";
private static final String PASSWORD = "your_password";
```

5. Соберите проект:

```bash
mvn clean install
```

6. Запустите сервер:

```bash
mvn exec:java -Dexec.mainClass="com.example.otp.App"
```

Сервер будет доступен по адресу: `http://localhost:8080`

---

## Основные эндпоинты

### 1. Регистрация пользователя или администратора

```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "adminpass",
    "role": "ADMIN"
}'
```

Ответ:

```
User registered successfully
```

---

### 2. Логин пользователя

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "adminpass"
}'
```

Ответ:

```json
{"token":"uuid-token-string"}
```

---

### 3. Генерация OTP-кода

```bash
curl -X POST http://localhost:8080/generate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "operationId": "confirm_email",
    "email": "test@example.com"
}'
```

Ответ:

```json
{"otp":"123456"}
```

OTP-код будет отправлен на указанный адрес электронной почты и будет также сохранён в файл проекта.

---

### 4. Проверка OTP-кода

```bash
curl -X POST http://localhost:8080/verify \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "operationId": "confirm_email",
    "code": "123456"
}'
```

Ответ:

```
Code is valid
```

Или:

```
Invalid or expired code
```

---

### 5. Получение конфигурации OTP (админ)

```bash
curl -X GET http://localhost:8080/admin/config \
  -H "Authorization: Bearer your_admin_token"
```

Ответ:

```json
{
  "codeLength": 6,
  "expirationMinutes": 5
}
```

---

### 6. Обновление конфигурации OTP (админ)

```bash
curl -X POST http://localhost:8080/admin/config \
  -H "Authorization: Bearer your_admin_token" \
  -H "Content-Type: application/json" \
  -d '{
    "codeLength": 4,
    "expirationMinutes": 2
}'
```

Ответ:

```
Configuration updated
```

---

### 7. Получение всех пользователей (админ)

```bash
curl -X GET http://localhost:8080/admin/users \
  -H "Authorization: Bearer your_admin_token"
```

Ответ:

```json
[
  { "id": 2, "username": "user1" },
  { "id": 3, "username": "user2" }
]
```

---

### 8. Удаление пользователя (админ)

```bash
curl -X DELETE http://localhost:8080/admin/users/username_to_delete \
  -H "Authorization: Bearer your_admin_token"
```

Ответ:

```
User deleted
```

---

## Структура проекта

```
src/
 └── main/
     └── java/
         └── com/
             └── example/
                 └── otp/
                     ├── auth/
                     ├── dao/
                     ├── handler/
                     ├── model/
                     ├── service/
                     └── util/
pom.xml
README.md
schema.sql
```



