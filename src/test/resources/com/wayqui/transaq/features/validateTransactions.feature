Feature: Transaction management (creation, validation, search)

  @validation
  Scenario Outline: Verify a nonexistent transaction

    Given A transaction that is not stored in our system
    When I check the status from <Channel> channel
    Then The system returns the status 'INVALID'
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |
      | INTERNAL |


  @validation
  Scenario Outline: Verify from a client or ATM a transaction stored before today in our system

    Given A transaction that is stored in our system
    And the transaction date is before today
    When I persist the transaction in database
    And I check the status from <Channel> channel
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |


  @validation
  Scenario: Verify from an internal channel a transaction stored before today in our system

    Given A transaction that is stored in our system
    And the transaction date is before today
    When I persist the transaction in database
    And I check the status from INTERNAL channel
    Then The system returns the status 'SETTLED'
    And the amount
    And the fee


  @validation
  Scenario Outline: Verify from a client or ATM a transaction stored today in our system

    Given A transaction that is stored in our system
    And the transaction date is equals to today
    When I persist the transaction in database
    And I check the status from <Channel> channel
    Then The system returns the status 'PENDING'
    And the amount substracting the fee
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |


  @validation
  Scenario: Verify from an internal channel a transaction stored today in our system

    Given A transaction that is stored in our system
    And the transaction date is equals to today
    When I persist the transaction in database
    And I check the status from INTERNAL channel
    Then The system returns the status 'PENDING'
    And the amount
    And the fee


  @validation
  Scenario: Verify from a client a transaction stored whose data is greater than today in our system

    Given A transaction that is stored in our system
    And the transaction date is greater than today
    When I persist the transaction in database
    And I check the status from CLIENT channel
    Then The system returns the status 'FUTURE'
    And the amount substracting the fee


  @validation
  Scenario: Verify from an ATM a transaction stored whose data is greater than today in our system

    Given A transaction that is stored in our system
    And the transaction date is greater than today
    When I persist the transaction in database
    And I check the status from ATM channel
    Then The system returns the status 'PENDING'
    And the amount substracting the fee


  @validation
  Scenario: Verify from an internal channel a transaction stored whose data is greater than today in our system

    Given A transaction that is stored in our system
    And the transaction date is greater than today
    When I persist the transaction in database
    And I check the status from INTERNAL channel
    Then The system returns the status 'FUTURE'
    And the amount
    And the fee



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
      |ES9820385778983000760236|2020-04-16T00:00:00.000Z|-20.4|2.00|Water bill|
      |ES9820385778983000760236|2020-04-12T00:00:00.000Z|50.25|1.00|Alex debt|
      |ES9820385778983000760236|2020-07-16T00:00:00.000Z|120.20|34.00|Transference from carlos|
      |ES9820385778983000760236|2020-04-16T00:00:00.000Z|60.00|1.5|HHEE at work|
      |ES9820385778983000760236|2020-07-14T00:00:00.000Z|-24.00|12.5|Dinner at Goikos|
      |ES9820385778983000760236|2020-01-20T00:00:00.000Z|-10.15|3.33|Restaurant Casa Dany|
      |ES9820385778983000760236|2020-07-22T00:00:00.000Z|4.65|0.15|Breakfast at Viena|


  @creation
  Scenario: Error when creating a transaction with amount zero

    Given the following information for creating a new transaction: ES9820385778983000900000, 2020-07-22T00:00:00.000Z, 0.00, 12 and Payment of debt
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Validation error'
    And with the specific validation error of 'amount cannot be zero'

  @creation
  Scenario: Error when creating a transaction with no IBAN

    Given the following information for creating a new transaction: , 2020-07-22T00:00:00.000Z, 17.4, 12 and Payment of debt
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Validation error'
    And with the specific validation error of 'account_iban cannot be empty'

  @creation
  Scenario: Error when creating a transaction with a negative fee

    Given the following information for creating a new transaction: ES9820385778983000900000, 2020-07-22T00:00:00.000Z, 17.4, -14 and Payment of debt
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Validation error'
    And with the specific validation error of 'fee must be positive'

  @creation
  Scenario: Error when creating a transaction with fee greater than amount

    Given the following information for creating a new transaction: ES9820385778983000900000, 2020-07-22T00:00:00.000Z, 17.4, 23 and Payment of debt
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Fee cannot have a greater value than amount'

  @creation
  Scenario: Error when creating a transaction that could lead to zero the account balance

    Given the following information for creating a new transaction: ES9820385778983000900000, 2020-07-22T00:00:00.000Z, -17.4, 12 and Payment of debt
    When I try to persist the transaction in database
    Then The service returns the HTTP status 'Bad Request'
    And the error message is 'Debit transaction not allowed since the current balance for the account is 0.0'

