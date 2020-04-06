# TransaQ : A Bank transaction handler microservice

## Start the application

```bash
mvn spring-boot:run
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

## Database configuration

There are configured for this microservice two H2 database connections, one for the application and the other for testing purposes. Current configuration stores both datasources in memory so you can start the microservice and execute the cucumber tests and te same time and there won't be any concurrency issue and the databases wont overlap each other. 

If you want to physically persis your application data you can change the attribute in src/main/resources/application.properties file by simply changing the comment location (and, of course, be sure that the path exists):

```bash
spring.datasource.url = jdbc:h2:file:/opt/data/transactiondb
# spring.datasource.url=jdbc:h2:mem:transactiondb
```

## Run acceptance tests

From command line you should type the following command to execute acceptance tests

```bash
mvn test (Working on it...)
```
