@ignore
Feature: Fetch WPP Auth Token

  Scenario: Get Token from Auth Service
    # Append '/system' because we are authenticating via systemId and secretKey
    Given url authUri + '/system'
    And request { systemId: '#(authSystemId)', secretKey: '#(authSecretKey)', maxAge: '#(authMaxAge)' }
    When method post
    Then status 200
    
    # Extract the token from the response headers
    * def authToken = responseHeaders['x-wpp-auth-token'][0]