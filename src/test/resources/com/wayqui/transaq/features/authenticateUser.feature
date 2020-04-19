Feature: Authentication scenarios

  @authentication
  Scenario Outline: Unauthenticated user wants to create a transaction
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Forbidden'
    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000760236|2020-04-08T00:00:00.000Z|9.25|3.40|Netflix|

  @authentication
  Scenario: Authenticate a user
    Given A user registered in our system
    When the user logs in
    Then The service returns the HTTP status 'OK'
    And a valid JWT token is generated