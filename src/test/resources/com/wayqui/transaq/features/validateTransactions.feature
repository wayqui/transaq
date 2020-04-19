Feature: Transaction validation scenarios

  Background: For validating the different scenarios in transaction management
    we'll consider that the're is a valid user registered in the system and
    has a valid JWT token generated

    Given A user registered in our system
    When the user logs in
    Then The service returns the HTTP status 'OK'
    And a valid JWT token is generated

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

