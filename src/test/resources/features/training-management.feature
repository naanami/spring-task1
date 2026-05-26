Feature: Training Management

  Scenario: Successfully create training
    Given valid training data
    When create training request is sent
    Then training should be created successfully

  Scenario: Create training with invalid data
    Given invalid training data
    When invalid create training request is sent
    Then bad request response should be returned