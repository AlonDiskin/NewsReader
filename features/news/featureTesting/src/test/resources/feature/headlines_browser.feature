Feature: News headlines browser

  #Rule: Show paged headlines

  @show-headlines
  Scenario: Headlines shown
    Given user has browsed current headlines
    When new headlines are available
    And user select to refresh content
    Then app should show refreshed headlines content

  @handle-headlines-error
  Scenario Outline: Headlines listing error handled
    Given an existing error of type "<error>"
    When user open headlines screen
    Then app should handle error as "<handling>"
    Examples:
      | error               | handling                             |
      | device connectivity | show device connection error message |
      | remote server       | show remote server error message     |
      | app internal error  | show app error message               |

  #Rule: Enable headline sharing

  @headline-shared
  Scenario: Headline shared
    Given user selected to share first shown headlines
    Then app should share headline vie device sharing menu

  #Rule: Enable article source reading

  @article-source-opened
  Scenario: Article source opened
    Given user selected to read first shown headlines in browser
    Then app should open article source in device default browser