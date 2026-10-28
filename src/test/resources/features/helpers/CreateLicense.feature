@ignore
Feature: Register License in DB Service

  Scenario: POST License Payload
    Given url dbServiceUrl
    And path 'v1/DeliveryLicenses/expanded'
    And request licensePayload
    When method post
    
    # Use native Java System.out to force the error into the console
    * if (responseStatus != 200 && responseStatus != 201) java.lang.System.out.println('\n\n❌ LICENSE REGISTRATION FAILED: ' + karate.pretty(response) + '\n\n')
    
    Then assert responseStatus == 200 || responseStatus == 201
    * print '✅ Successfully registered license:', response.id