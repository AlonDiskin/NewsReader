Feature: User journey to browse latest news headlines

  Scenario: User share headline
    Given user launched app from device home
    When user open news headlines screen
    And share first listed headline
    Then app should display device sharing menu