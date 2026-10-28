@qa @healthcheck
Feature: Hermes DPL Services Health Check

  Background:
    # karate.env defaults to 'qa' if not passed in the command line
    * def envSuffix = karate.env ? karate.env : 'qa'

  Scenario Outline: Verify <name> service is up and running
    * def serviceUrl = 'http://' + hostPrefix + '-' + envSuffix + '.aws.wiley.com:8080'
    
    Given url serviceUrl
    And path 'version'
    When method get
    Then status 200
    And match response contains
      """
      {
        component: '#string',
        environment: '#string',
        buildVersion: '#string',
        startTime: '#string',
        serverIp: '#string'
      }
      """

    Examples:
      | name        | hostPrefix                     |
      | Book API    | dpl-book-api                   |
      | FTP         | dpl-ftp-service                |
      | Pull        | dpl-pull-service               |
      | Gateway     | dpl-gateway-service            |
      | Pack        | dpl-pack-service               |
      | Delivery DB | dpl-delivery-db-service        |
      | Send        | dpl-send-service               |
      | Cleanup     | dpl-cleanup-service            |
      | Notify      | dpl-notify-service             |
      | Onix        | dpl-onix-service               |
      | Cover       | dpl-cover-service              |
      | Onix Update | dpl-onix-update-service        |
    #   | Postponed   | dpl-postponed-delivery-service |  <- Server has decommisioned
      | UI          | dpl-ui-service                 |