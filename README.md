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
mvn test
```

The command from above executes all scenarios implemented. 

However, I have tagged the test in two groups so far, one with the scenarios related to the status **validation** and the other related with the **creation** of transactions; if you want to launch one or another just execute the following command:

```bash
mvn test -Dcucumber.filter.tags="@validation"
```
Or 
```bash
mvn test -Dcucumber.filter.tags="@creation"
```

## Business requirements and assumptions

The business functionality for this service is very clear and well detailed in the documentation (both in Cucumber scenarios and documentation provided); however, there are some aspects of it that, during development and testing, sounded ambiguous to me, that's why I labeled those issues in the code with comments including the word ***ASSUMPTION*** so, if you search this keyword, you'll find the places where I assumed certain things.
