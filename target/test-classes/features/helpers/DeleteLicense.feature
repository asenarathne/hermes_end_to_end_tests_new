@ignore
Feature: Delete License from DB Service

  Scenario: DELETE License by ID
    Given url dbServiceUrl
    And path 'v1/DeliveryLicenses', licenseId
    When method delete
    
    # Print a warning if it fails, but DO NOT assert! This prevents teardowns from crashing tests.
    * if (responseStatus != 200 && responseStatus != 204 && responseStatus != 404) java.lang.System.out.println('\n⚠️ DB Cleanup Warning for ' + licenseId + ': ' + responseStatus)