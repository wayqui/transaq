Feature: Authentication scenarios

  @authentication
  Scenario Outline: Unauthenticated user wants to create a transaction
    Given A user not logged in our system
    And the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Unauthorized'
    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000760236|2020-04-08T00:00:00.000Z|9.25|3.40|Netflix|

  @authentication
  Scenario: Authenticate a user
    Given A user not logged in our system
    But the user is registered in our system
    When I send the user and password to the application
    Then The service returns the HTTP status 'OK'
    And I receive a JWT token