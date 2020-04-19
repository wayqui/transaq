# TransaQ : A Bank transaction handler microservice

This microservice handles transactions for users, the following operations are implemented:

* Create transaction.
* Search transactions by IBAN
* Verify the status of a transaction from a channel

## Start the application

```bash
mvn spring-boot:run
```

## Endpoint: Authentication and use

After started, the service will be available from:

```bash
http://localhost:8080/transaq/rest/
```

Regarding the authentication, the service has a **basic auth** implemented with credentials:

```bash
username = appuser
password = pwdappuser
```

Just make a GET request to the /rest/login path with the credentials included in the basic auth header:

```bash
~ % curl --location --request GET 'http://localhost:8080/transaq/rest/login' \
--header 'Accept: application/json' \
--header 'Authorization: Basic YXBwdXNlcjpwd2RhcHB1c2Vy' -i
HTTP/1.1 200
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcHB1c2VyIiwiZXhwIjoxNTg3MzAzNTgzLCJpYXQiOjE1ODcyODU1ODN9.Tn90iJz1oDdnFWEPMCdcCFgTcqayFtKoFglkvukCpc6lOeOrX-Cx3hw32JaNz5h_qPB2n4nLCc0rBbw-T0up3w
X-Content-Type-Options: nosniff
```
If the credentials are correct, the JWT token is included in the header of the response. You now can use this token to execute the rest of services.

## Available services

You could call it using CURL as follows:

### Create transaction

```bash
curl --location --request POST 'http://localhost:8080/transaq/rest/transaction/' \
--header 'Accept: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcHB1c2VyIiwiZXhwIjoxNTg3MzAyNTMxLCJpYXQiOjE1ODcyODQ1MzF9.Y5_DNsDeFX-za_B-wOGrabz5fvW-OXeKwaQvuvGHptex08cR83Q_1JWha64dG_FOPg4C9yv5BLMM3O1C_CsEbA' \
--data-raw '{
    "account_iban": "ES9820385778983000760236",
    "date": "2020-07-16T16:55:42.000Z",
    "amount": 3.00,
    "fee": 1.18,
    "description": "Restaurant payment"
}'
```

### Obtain transaction's status

```bash
curl --location --request POST 'http://localhost:8080/transaq/rest/transaction/status' \
--header 'Accept: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcHB1c2VyIiwiZXhwIjoxNTg3MzAyNTMxLCJpYXQiOjE1ODcyODQ1MzF9.Y5_DNsDeFX-za_B-wOGrabz5fvW-OXeKwaQvuvGHptex08cR83Q_1JWha64dG_FOPg4C9yv5BLMM3O1C_CsEbA' \
--data-raw '{
    "reference": "ddcdcd9b-852a-4eaf-b162-bd71d4dc9582",
    "channel": "CLIENT"
}'
```

### Search transactions

```bash
curl --location --request GET 'http://localhost:8080/transaq/rest/transaction?account_iban=ES9820385778983000760236&ascending=false' \
--header 'Accept: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhcHB1c2VyIiwiZXhwIjoxNTg3MzAyNTMxLCJpYXQiOjE1ODcyODQ1MzF9.Y5_DNsDeFX-za_B-wOGrabz5fvW-OXeKwaQvuvGHptex08cR83Q_1JWha64dG_FOPg4C9yv5BLMM3O1C_CsEbA' \
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

However, I have tagged the test in three groups so far, for **validation**, **creation** and **authentication**; if you want to launch any of those just execute the following command:

```bash
mvn test -Dcucumber.filter.tags="@validation"
```
Or 
```bash
mvn test -Dcucumber.filter.tags="@creation"
```
Or
```bash
mvn test -Dcucumber.filter.tags="@authentication"
```

## Business requirements and assumptions

The business functionality for this service is very clear and well detailed in the documentation (both in Cucumber scenarios and documentation provided); however, there are some aspects of it that, during development and testing, sounded ambiguous to me, that's why I labeled those issues in the code with comments including the word ***ASSUMPTION*** so, if you search this keyword, you'll find the places where I assumed certain things.
