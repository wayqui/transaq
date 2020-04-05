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
