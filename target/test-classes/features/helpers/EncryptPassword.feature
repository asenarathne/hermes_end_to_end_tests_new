@ignore
Feature: Encrypt Password via DB Service

  Scenario: GET Encrypted Password
    # The calling script provides a 'rawPassword' variable
    Given url dbServiceUrl
    And path 'v1/encrypt', rawPassword
    When method get
    Then status 200
    
    # Store the plain text response (the encrypted hash) into a variable
    * def encryptedPassword = response