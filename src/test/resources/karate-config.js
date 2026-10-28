function fn() {
  var env = karate.env || 'qa';
  karate.log('Running Karate tests in environment:', env);

  var config = {
    env: env,

    // --- Core REST Endpoints ---
    dbServiceUrl: 'http://dpl-delivery-db-service-qa.aws.wiley.com:8080',
    onixServiceUrl: 'http://dpl-onix-service-qa.aws.wiley.com:8080',
    coverServiceUrl: 'http://dpl-cover-service-qa.aws.wiley.com:8080',
    productApiUrl: 'http://ctpm-product-api-qa.aws.wiley.com:8080/v1',
    productionApiUrl: 'http://cmh-production-api-qa.aws.wiley.com:8080/v1/productions/',
    jobApiUrl: 'http://cmh-job-api-qa.aws.wiley.com:8080/v1',
    contentApiUrl: 'http://dcm-content-api-qa.aws.wiley.com:8080/api/v1',
    notificationServiceUrl: 'http://wpp-ntf-svc-wqa.aws.wiley.com:8080',
    uiServiceUrl: 'http://dpl-ui-service-qa.aws.wiley.com:8080',
    wiremockUrl: 'http://dpl-wiremock-server-qa.aws.wiley.com:8080',

    // --- Agent JWT Config (for DBService X-WPP-AUTH-TOKEN) ---
    agentEncoderKey: 'dpl-qa',
    agentId: 'integration.test.agent.id',

    // --- WPP Auth Service ---
    authUri: 'http://wpp-auth-svc-wqa.aws.wiley.com:8080/v1/auth/authenticate',
    authSystemId: java.lang.System.getenv('AUTH_SYSTEM_ID') || '77fcd692-4ff0-47a0-b824-c1d3c54d349a',
    authSecretKey: java.lang.System.getenv('AUTH_SECRET_KEY') || 'P03IVwLH4bzKWmaO8kSISERqkTrNbnci',
    authMaxAge: 1800,

    // --- All 14 DPL Microservices for HealthCheck ---
    services: [
      { name: 'Book API',    url: 'http://dpl-book-api-qa.aws.wiley.com:8080' },
      { name: 'FTP',         url: 'http://dpl-ftp-service-qa.aws.wiley.com:8080' },
      { name: 'Pull',        url: 'http://dpl-pull-service-qa.aws.wiley.com:8080' },
      { name: 'Gateway',     url: 'http://dpl-gateway-service-qa.aws.wiley.com:8080' },
      { name: 'Pack',        url: 'http://dpl-pack-service-qa.aws.wiley.com:8080' },
      { name: 'Delivery DB', url: 'http://dpl-delivery-db-service-qa.aws.wiley.com:8080' },
      { name: 'Send',        url: 'http://dpl-send-service-qa.aws.wiley.com:8080' },
      { name: 'Cleanup',     url: 'http://dpl-cleanup-service-qa.aws.wiley.com:8080' },
      { name: 'Notify',      url: 'http://dpl-notify-service-qa.aws.wiley.com:8080' },
      { name: 'Onix',        url: 'http://dpl-onix-service-qa.aws.wiley.com:8080' },
      { name: 'Cover',       url: 'http://dpl-cover-service-qa.aws.wiley.com:8080' },
      { name: 'Onix Update', url: 'http://dpl-onix-update-service-qa.aws.wiley.com:8080' },
      { name: 'Postponed',   url: 'http://dpl-postponed-delivery-service-qa.aws.wiley.com:8080' },
      { name: 'UI',          url: 'http://dpl-ui-service-qa.aws.wiley.com:8080' }
    ],

    // --- AWS S3 ---
    s3Bucket: 'aws-nonprod-deliveryplatform-qa',
    s3Endpoint: 'https://s3.amazonaws.com',
    s3Region: 'us-east-1',

    // --- FTP & SFTP ---
    ftpServiceHost: 'dpl-ftp-service-qa.aws.wiley.com',
    ftpServicePort: 8721,
    sftpHost: 'xferdcint.wiley.com',
    sftpPort: 22,
    sftpUser: 'dpl_qa',
    sftpPassword: 'qlQCqIdm',

    // --- TIBCO EMS ---
    tibcoUrl: 'tcp://emsqaaus01.wiley.com:7222,tcp://emsqaaus02.wiley.com:7222',
    tibcoUsername: java.lang.System.getenv('TIBCO_USER') || 'dplclient',
    tibcoPassword: java.lang.System.getenv('TIBCO_PASSWORD') || 'dplclient',
    deliveryTopic: 'wiley.global.delivery.topic',
    onixTopic: 'wiley.global.dpl.onix.topic',
    gatewayRequestQueue: 'wiley.global.dpl.gateway_request.queue'
  };

  // --- Global WPP Authentication ---
  // Call Auth.feature once and cache the result for all parallel threads
  var authResult = karate.callSingle('classpath:features/helpers/Auth.feature', config);
  
  // Globally set the X-WPP-AUTH-TOKEN header for all HTTP requests
  karate.configure('headers', { 'X-WPP-AUTH-TOKEN': authResult.authToken });

  karate.configure('connectTimeout', 30000);
  karate.configure('readTimeout', 30000);

  return config;
}