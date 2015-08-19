# boloid-test-api - Сервер API (JSON HTTP API)

## Описание API

#### 1. Загрузчик

* url - /upload
* HTTP method - POST
* Файл передается в виде Multipart

Пример кода для тестирование:

    given()
        .multiPart(new File("test/images/test1.jpeg"))
    .when()
        .post("/upload")
    .then()
        .body("success", is(true))
        .body("result", notNullValue(String.class));

#### 2. Добавление нового пользователя

* url - /user
* HTTP method - POST
* Параметры передаются в Body в формате json

Пример параметров:

    {
        photoUri: '/201512/123123.jpeg',
        login: 'test_1',
        email: 'test_1@test.com',
        status: 'Online'
    }

Пример кода для тестирования:

    User u = new User();
    u.setEmail("test_1@te.com");
    u.setLogin("test_1");
    u.setStatus("Online");

    given()
        .contentType("application/json")
        .body(u)
    .when()
        .post("/user")
    .then()
        .body("result.id", notNullValue(String.class));

#### 3. Получение информации о пользователе

* url - /user/{userId}, где {userId} - id пользователя
* HTTP method - GET

Пример кода для тестирования:

    when()
        .get("/user/{userId}", "test_2")
    .then()
        .body("success", is(true))
        .body("result.id", is("test_2"));

#### 4. Изменение статуса пользователя (Online, Offline)

* url - /userstatus
* HTTP method - POST
* Параметры передаются в Body в формате json

Пример параметров:

    {
        userId: '123123',
        status: 'Online'
    }

Пример кода для тестирования:

    given()
        .contentType("application/json")
        .body(params)
    .when()
        .post("/userstatus")
    .then()
        .body("result", notNullValue())
        .body("result.userId", is("test_3"))
        .body("result.oldStatus", is("Online"))
        .body("result.newStatus", is("Offline"));

#### 5. Статистика сервера

* url - /statistics
* HTTP method - POST
* Параметры передаются в Body в формате json

Пример параметров:

    {
        status: 'Online',
        timestamp: '2015-08-10 21:00:00,999' //format yyyy-MM-dd HH:mm:ss,SSS
    }

Пример кода для тестирования:

    given()
        .contentType("application/json")
        .body(params)
    .when()
        .post("/statistics")
    .then()
        .body("result", notNullValue())
        .body("result", isA(List.class))
        .body("result.size()", is(2));

## Трудозатраты
1. Проектирование - 3ч
2. Разработка - 6ч
3. Тестирование - 5ч