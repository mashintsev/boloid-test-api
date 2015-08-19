# boloid-test-api - Сервер API (JSON HTTP API)

## Трудозатраты
1. Проектирование - 3ч
2. Разработка - 6ч
3. Тестирование - 5ч

## Описание API

1. Загрузчик

'''
url - /upload
HTTP method - POST
Файл передается в виде Multipart
'''

Пример кода для тестирование:
'''
given().multiPart(new File("test/images/test1.jpeg"))
                .when()
                .post("/upload")
                .then()
                .body("success", is(true))
                .body("result", notNullValue(String.class));
'''