Feature: Transaction validation from different channels

  Scenario Outline: Verify a nonexistent transaction
    Given A transaction that is not stored in our system
  When I check the status from <Channel>
  Then The system returns the status 'INVALID'
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |
      | INTERNAL |

  Scenario Outline: Verify from a client or ATM a transaction stored before today in our system
    Given A transaction that is stored in our system
    When I check the status from <Channel>
    And the transaction date is before today
    Then The system returns the status 'SETTLED'
    And the amount substracting the fee
    Examples:
      | Channel  |
      | CLIENT   |
      | ATM      |
