@qa @local @dev @vch-onix-ref
Feature: VCH Onix Reference

  Background:
    # Initialize Java helpers
    * def S3 = Java.type('com.wiley.hermes.helpers.S3Helper')
    * def FtpHelper = Java.type('com.wiley.hermes.helpers.FtpHelper')
    * def Tibco = Java.type('com.wiley.hermes.helpers.TibcoHelper')
    
    # Utility functions for strict DPL payload validation
    * def uuid = function(){ return java.util.UUID.randomUUID().toString() }
    * def now = function(){ return java.time.Instant.now().toString() }
    * def agent = { modelType: 'System', id: 'integration.test.agent.id' }

  @TestRailId(5503361) @s3
  Scenario Outline: VCH Onix Reference S3
    * def uniqueSuffix = java.lang.System.currentTimeMillis() + ''
    # Keep the ID under 36 characters!
    * def licenseId = 'vchonixs3-' + uniqueSuffix
    
    * def destinationPath = '<destination>'
    * def expectedFile = '<expected>'
    * def s3InputKey = 'inputFiles/vch/vch_onix/print/reference/wileyDE_3.1_incremental_02_13_2026.xml'
    
    # 1. Define Template Variables
    * def licenseDescription = 'Karate VCH Onix Reference S3 License'
    * def triggerEvent = 'VCH_ONIX_REFERENCE'
    * def deliveryFormat = 'noTransformation'
    * def destinationProtocol = 's3'
    * def destinationUrl = s3Endpoint + '/' + s3Bucket + destinationPath

    # 2. Read the S3 template
    * def licensePayload = read('classpath:data/templates/s3-license-template.json')

    # 3. Register License and Configure Cleanup
    * call read('classpath:features/helpers/CreateLicense.feature') { licensePayload: '#(licensePayload)' }
    * configure afterScenario = function(){ karate.call('classpath:features/helpers/DeleteLicense.feature', { licenseId: licenseId }) }

    # 4. Clean up S3 destination and input paths
    * S3.deleteObject(s3Bucket, s3InputKey)
    * S3.deleteObject(s3Bucket, destinationPath + '/' + expectedFile)

    # 5. Trigger the flow by uploading the ONIX XML to S3
    * S3.uploadResource(s3Bucket, s3InputKey, '/data/vch/wileyDE_3.1_incremental_02_13_2026.xml')

    # 6. Listen to TIBCO EMS for DELIVERY_ENDED
    * assert Tibco.waitForEvent(tibcoUrl, tibcoUsername, tibcoPassword, deliveryTopic, 'DELIVERY_ENDED', 120)

    # 7. Verify the file landed in the S3 destination folder
    * assert S3.waitForFile(s3Bucket, destinationPath, expectedFile, 60)

    @TestRailId(453221)
    Examples:
      | licenseName        | destination                     | expected                               |
      | VchOnixReferenceS3 | /dpl-end-to-end-test/POD/Amazon | wileyDE_3.1_incremental_02_13_2026.xml |


  @ftp
  Scenario Outline: VCH Onix Reference FTP
    * def uniqueSuffix = java.lang.System.currentTimeMillis() + ''
    # Keep the ID under 36 characters!
    * def licenseId = 'vchonixftp-' + uniqueSuffix
    
    * def destinationPath = '<destination>'
    * def expectedFile = '<expected>'
    * def ftpUploadPath = '/vch_onix/print/reference/wileyDE_3.1_incremental_02_13_2026.xml'
    
    # 1. Encrypt the FTP password via the DB Service helper
    * def encryption = call read('classpath:features/helpers/EncryptPassword.feature') { rawPassword: 'pullPassword' }
    * def securePassword = encryption.encryptedPassword

    # 2. Define Template Variables
    * def licenseDescription = 'Karate VCH Onix Reference FTP License'
    * def triggerEvent = 'VCH_ONIX_REFERENCE'
    * def deliveryFormat = 'noTransformation'
    * def destinationProtocol = 's3'
    * def destinationUrl = s3Endpoint + '/' + s3Bucket + destinationPath

    # 3. Read the FTP template
    * def licensePayload = read('classpath:data/templates/ftp-license-template.json')
    
    # 4. Register License and Configure Cleanup
    * call read('classpath:features/helpers/CreateLicense.feature') { licensePayload: '#(licensePayload)' }
    * configure afterScenario = function(){ karate.call('classpath:features/helpers/DeleteLicense.feature', { licenseId: licenseId }) }

    # 5. Clean up existing FTP file and Upload new RAW XML
    * FtpHelper.deleteRemoteFile(ftpServiceHost, ftpServicePort, 'vch', 'vchpass', ftpUploadPath)
    * FtpHelper.uploadRawResource(ftpServiceHost, ftpServicePort, 'vch', 'vchpass', '/data/vch/wileyDE_3.1_incremental_02_13_2026.xml', ftpUploadPath)

    # 6. Listen to TIBCO EMS for DELIVERY_ENDED
    * assert Tibco.waitForEvent(tibcoUrl, tibcoUsername, tibcoPassword, deliveryTopic, 'DELIVERY_ENDED', 120)

    @TestRailId(453221)
    Examples:
      | licenseName         | destination                     | expected                               |
      | VchOnixReferenceFTP | /dpl-end-to-end-test/POD/Amazon | wileyDE_3.1_incremental_02_13_2026.xml |