# TransaQ : A Bank transaction handler microservice

## Start the application

```bash
mvn spring-boot:run
```

## Run acceptance tests

```bash
mvn test
```

## Endpoint

After started, the service will be available from:

```bash
http://localhost:8080/transaq/rest/
```

You could call it using CURL as follows:

```bash
curl --location --request GET 'http://localhost:8080/transaq/rest/transaction/status' \
--header 'Content-Type: application/json' \
--header 'Accept: application/json' \
--header 'Content-Type: application/json'
```

