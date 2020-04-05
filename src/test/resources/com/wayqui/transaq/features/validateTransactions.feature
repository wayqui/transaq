Feature: Transaction validation from different channels

  Scenario Outline: Verify a nonexistent transaction

    Given A transaction that is not stored in our system
    When I check the status from <Channel> channel
    Then The system returns the status 'INVALID'
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |
      | INTERNAL |


  Scenario Outline: Verify from a client or ATM a transaction stored before today in our system

    Given A transaction that is stored in our system
    And the transaction date is before today
    When I check the status from <Channel> channel
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |


  Scenario: Verify from an internal channel a transaction stored before today in our system

    Given A transaction that is stored in our system
    And the transaction date is before today
    When I check the status from INTERNAL channel
    Then The system returns the status 'SETTLED'
    And the amount
    And the fee


  Scenario Outline: Verify from a client or ATM a transaction stored today in our system
    Given A transaction that is stored in our system
    And the transaction date is equals to today
    When I check the status from <Channel> channel
    Then The system returns the status 'PENDING'
    And the amount substracting the fee
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |
