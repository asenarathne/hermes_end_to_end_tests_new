@qa @vault
Feature: Vault License DB Check

  Scenario: Verify we can authenticate and fetch a license
    # Hit the exact endpoint your legacy MigrateLicense.java used
    Given url dbServiceUrl
    And path 'v1/DeliveryLicenses/TestLicense/expanded'
    When method get
    # It might be 200 if it exists, or 404 if it doesn't - both prove Auth works!
    Then assert responseStatus == 200 || responseStatus == 404