Feature: Transaction creation scenarios

  Background: For validating the different scenarios in transaction management
  we'll consider that the're is a valid user registered in the system and
  with the right credentials

    Given A user registered in our system
    When the user logs in
    Then The service returns the HTTP status 'OK'
    And a valid JWT token is generated

  @creation
  Scenario Outline: Create several transactions associated to the same IBAN
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I persist the transaction in database
    Then The service returns the HTTP status 'Created'
    And the transaction reference is informed

    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000760236|2020-04-08T00:00:00.000Z|9.25|3.40|Netflix|
      |ES9820385778983000760236|2020-05-16T00:00:00.000Z|46.54|12.45|Reimbursement for purchase|
      |ES9820385778983000760236|2020-04-16T00:00:00.000Z|-20.40|2.00|Water bill|
      |ES9820385778983000760236|2020-04-12T00:00:00.000Z|50.25|1.00|Alex debt|
      |ES9820385778983000760236|2020-07-16T00:00:00.000Z|120.20|34.00|Transference from carlos|
      |ES9820385778983000760236|2020-04-16T00:00:00.000Z|60.00|1.50|HHEE at work|
      |ES9820385778983000760236|2020-07-14T00:00:00.000Z|-24.00|12.50|Dinner at Goikos|
      |ES9820385778983000760236|2020-01-20T00:00:00.000Z|-10.15|3.33|Restaurant Casa Dany|
      |ES9820385778983000760236|2020-07-22T00:00:00.000Z|4.65|0.15|Breakfast at Viena|



  @creation
  Scenario Outline: Error when creating a transaction with amount zero
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Validation error'
    And with the specific validation error of 'amount cannot be zero'
    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000900000|2020-07-22T00:00:00.000Z|0.00|12|Payment of debt|


  @creation
  Scenario Outline: Error when creating a transaction with no IBAN
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Validation error'
    And with the specific validation error of 'account_iban cannot be empty'
    Examples:
      |account_iban|date|amount|fee|description|
      ||2020-07-22T00:00:00.000Z|17.4|12|Payment of debt|


  @creation
  Scenario Outline: Error when creating a transaction with a negative fee
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Validation error'
    And with the specific validation error of 'fee must be positive'
    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000900000|2020-07-22T00:00:00.000Z|17.4|-14|Payment of debt|


  @creation
  Scenario Outline: Error when creating a transaction with fee greater than amount
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Fee cannot have a greater value than amount'
    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000900000|2020-07-22T00:00:00.000Z|17.4|23|Payment of debt|


  @creation
  Scenario Outline: Error when creating a transaction that could lead to zero the account balance
    Given the following information for creating a new transaction: <account_iban>, <date>, <amount>, <fee> and <description>
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Transaction forbidden, the current balance for the account is 0'
    Examples:
      |account_iban|date|amount|fee|description|
      |ES9820385778983000900000|2020-07-22T00:00:00.000Z|-17.4|12|Payment of debt|
