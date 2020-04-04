Feature: Transaction validation from different channels

  Scenario: Verify a nonexistent transaction
    Given A transaction that is not stored in our system
  When I check the status from any channel
  Then The system returns the status 'INVALID'

  Scenario: Verify from a client or ATM a transaction stored before today in our system
    Given A transaction that is stored in our system
  When I check the status from CLIENT or ATM channel
    And the transaction date is before today
  Then The system returns the status 'SETTLED'
    And the amount substracting the fee

  Scenario: Verify from an internal channel a transaction stored before today in our system
    Given A transaction that is stored in our system
  When I check the status from INTERNAL channel
    And the transaction date is before today
  Then The system returns the status 'SETTLED'
    And the amount
    And the fee

  Scenario: Verify from a client or ATM a transaction stored today in our system
    Given A transaction that is stored in our system
  When I check the status from CLIENT or ATM channel
    And the transaction date is equals to today
  Then The system returns the status 'PENDING'
    And the amount substracting the fee

  Scenario: Verify from an internal channel a transaction stored today in our system
    Given A transaction that is stored in our system
  When I check the status from INTERNAL channel
    And the transaction date is equals to today
  Then The system returns the status 'PENDING'
    And the amount
    And the fee

  Scenario: Verify from a client a transaction stored whose data is greater than today in our system
    Given A transaction that is stored in our system
  When I check the status from CLIENT channel
    And the transaction date is greater than today
  Then The system returns the status 'FUTURE'
    And the amount substracting the fee

  Scenario: Verify from an ATM a transaction stored whose data is greater than today in our system
    Given A transaction that is stored in our system
  When I check the status from ATM channel
    And the transaction date is greater than today
  Then The system returns the status 'PENDING'
    And the amount substracting the fee

  Scenario: Verify from an internal channel a transaction stored whose data is greater than today in our system
    Given A transaction that is stored in our system
  When I check the status from INTERNAL channel
    And the transaction date is greater than today
  Then The system returns the status 'FUTURE'
    And the amount
    And the fee