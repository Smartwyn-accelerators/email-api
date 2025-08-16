package com.fastcode.emailApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EmailApiPropertiesConfiguration {

    @Autowired
    private EmailApiLoggingHelper logHelper;

    @Autowired
    private Environment env;

    private static final String EMAIL_PROVIDER_DEFAULT_ENV = "EMAIL_PROVIDER_DEFAULT";
    private static final String EMAIL_PROVIDER_DEFAULT_SYSPROP = "email.provider.default";

    private static final String EMAIL_ADDRESS_FROM_ENV = "EMAIL_ADDRESS_FROM";
    private static final String EMAIL_ADDRESS_FROM_SYSPROP = "email.address.from";

    private static final String AWS_ACCESS_KEY_ID_ENV = "AWS_ACCESS_KEY_ID";
    private static final String AWS_ACCESS_KEY_ID_SYSPROP = "aws.accessKeyId";

    private static final String AWS_SECRET_ACCESS_KEY_ENV = "AWS_SECRET_ACCESS_KEY";
    private static final String AWS_SECRET_ACCESS_KEY_SYSPROP = "aws.secretKey";

    private static final String AWS_DEFAULT_REGION_ENV = "AWS_DEFAULT_REGION";
    private static final String AWS_DEFAULT_REGION_SYSPROP = "aws.region";

    private static final String SENDGRID_API_KEY_ENV = "SENDGRID_API_KEY";
    private static final String SENDGRID_API_KEY_SYSPROP = "sendgrid.api.key";

    private static final String MAILGUN_API_KEY_ENV = "MAILGUN_API_KEY";
    private static final String MAILGUN_API_KEY_SYSPROP = "mailgun.api.key";

    private static final String MAILGUN_API_DOMAIN_ENV = "MAILGUN_API_DOMAIN";
    private static final String MAILGUN_API_DOMAIN_SYSPROP = "mailgun.api.domain";

    private static final String MAILGUN_API_BASEURL_ENV = "MAILGUN_API_BASEURL";
    private static final String MAILGUN_API_BASEURL_SYSPROP = "mailgun.api.baseurl";

    private static final String RATE_LIMIT_EMAILS_PER_SECOND_ENV = "RATE_LIMIT_EMAILS_PER_SECOND";
    private static final String RATE_LIMIT_EMAILS_PER_SECOND_SYSPROP = "rate.limit.emails.per.second";

    private static final String MAX_RETRY_ATTEMPTS_ENV = "MAX_RETRY_ATTEMPTS";
    private static final String MAX_RETRY_ATTEMPTS_SYSPROP = "max.retry.attempts";

    private static final String RETRY_ATTEMPTS_INTERVAL_ENV = "RETRY_ATTEMPTS_INTERVAL";
    private static final String RETRY_ATTEMPTS_INTERVAL_SYSPROP = "retry.attempts.interval";

    private static final String EMAIL_THREADPOOL_CORE_POOL_SIZE_ENV = "EMAIL_THREADPOOL_CORE_POOL_SIZE";
    private static final String EMAIL_THREADPOOL_CORE_POOL_SIZE_SYSPROP = "email.threadpool.core-pool-size";

    private static final String EMAIL_THREADPOOL_MAX_POOL_SIZE_ENV = "EMAIL_THREADPOOL_MAX_POOL_SIZE";
    private static final String EMAIL_THREADPOOL_MAX_POOL_SIZE_SYSPROP = "email.threadpool.max-pool-size";

    private static final String EMAIL_THREADPOOL_QUEUE_CAPACITY_ENV = "EMAIL_THREADPOOL_QUEUE_CAPACITY";
    private static final String EMAIL_THREADPOOL_QUEUE_CAPACITY_SYSPROP = "email.threadpool.queue-capacity";

    private static final String EMAIL_THREADPOOL_KEEP_ALIVE_SECONDS_ENV = "EMAIL_THREADPOOL_KEEP_ALIVE_SECONDS";
    private static final String EMAIL_THREADPOOL_KEEP_ALIVE_SECONDS_SYSPROP = "email.threadpool.keep-alive-seconds";

    private static final String EMAIL_THREADPOOL_THREAD_NAME_PREFIX_ENV = "EMAIL_THREADPOOL_THREAD_NAME_PREFIX";
    private static final String EMAIL_THREADPOOL_THREAD_NAME_PREFIX_SYSPROP = "email.threadpool.thread-name-prefix";


    /**
     * @return the default email provider
     */
    public String getDefaultEmailProvider() {
        return getConfigurationProperty(EMAIL_PROVIDER_DEFAULT_ENV, EMAIL_PROVIDER_DEFAULT_SYSPROP, "SMTP");
    }

    /**
     * @return the email address from
     */
    public String getFromAddress() {
        return getConfigurationProperty(EMAIL_ADDRESS_FROM_ENV, EMAIL_ADDRESS_FROM_SYSPROP, "");
    }

    /**
     * @return the AWS access key ID
     */
    public String getAwsAccessKeyId() {
        return getConfigurationProperty(AWS_ACCESS_KEY_ID_ENV, AWS_ACCESS_KEY_ID_SYSPROP, "");
    }

    /**
     * @return the AWS secret access key
     */
    public String getAwsSecretAccessKey() {
        return getConfigurationProperty(AWS_SECRET_ACCESS_KEY_ENV, AWS_SECRET_ACCESS_KEY_SYSPROP, "");
    }

    /**
     * @return the AWS region
     */
    public String getAwsRegion() {
        return getConfigurationProperty(AWS_DEFAULT_REGION_ENV, AWS_DEFAULT_REGION_SYSPROP, "us-east-1");
    }

    /**
     * @return the SendGrid API KEY
     */
    public String getSendGridApiKey() {
        return getConfigurationProperty(SENDGRID_API_KEY_ENV, SENDGRID_API_KEY_SYSPROP, "");
    }

    /**
     * @return the Mailgun API KEY
     */
    public String getMailgunApiKey() {
        return getConfigurationProperty(MAILGUN_API_KEY_ENV, MAILGUN_API_KEY_SYSPROP, "");
    }

    /**
     * @return the Mailgun API Domain
     */
    public String getMailgunApiDomain() {
        return getConfigurationProperty(MAILGUN_API_DOMAIN_ENV, MAILGUN_API_DOMAIN_SYSPROP, "");
    }

    /**
     * @return the Mailgun API Domain
     */
    public String getMailgunApiBaseUrl() {
        return getConfigurationProperty(MAILGUN_API_BASEURL_ENV, MAILGUN_API_BASEURL_SYSPROP, "");
    }

    /**
     * @return the Retry Attempts
     */
    public int getMaxRetryAttempts() {
        return Integer.parseInt(getConfigurationProperty(MAX_RETRY_ATTEMPTS_ENV, MAX_RETRY_ATTEMPTS_SYSPROP, "3"));
    }
    /**
     * @return the Retry Attempts Interval
     */
    public int getRetryAttemptsInterval() {
        return Integer.parseInt(getConfigurationProperty(RETRY_ATTEMPTS_INTERVAL_ENV, RETRY_ATTEMPTS_INTERVAL_SYSPROP, "3"));
    }

    /**
     * @return the RateLimit Emails per second
     */
    public double getRateLimitEmailsPerSecond() {
        return Double.parseDouble(getConfigurationProperty(RATE_LIMIT_EMAILS_PER_SECOND_ENV, RATE_LIMIT_EMAILS_PER_SECOND_SYSPROP, "5"));
    }

    /**
     * @return the core pool size for the email thread pool
     */
    public int getEmailThreadPoolCorePoolSize() {
        return Integer.parseInt(getConfigurationProperty(EMAIL_THREADPOOL_CORE_POOL_SIZE_ENV, EMAIL_THREADPOOL_CORE_POOL_SIZE_SYSPROP, "10"));
    }

    /**
     * @return the max pool size for the email thread pool
     */
    public int getEmailThreadPoolMaxPoolSize() {
        return Integer.parseInt(getConfigurationProperty(EMAIL_THREADPOOL_MAX_POOL_SIZE_ENV, EMAIL_THREADPOOL_MAX_POOL_SIZE_SYSPROP, "50"));
    }

    /**
     * @return the queue capacity for the email thread pool
     */
    public int getEmailThreadPoolQueueCapacity() {
        return Integer.parseInt(getConfigurationProperty(EMAIL_THREADPOOL_QUEUE_CAPACITY_ENV, EMAIL_THREADPOOL_QUEUE_CAPACITY_SYSPROP, "500"));
    }

    /**
     * @return the keep-alive seconds for the email thread pool
     */
    public int getEmailThreadPoolKeepAliveSeconds() {
        return Integer.parseInt(getConfigurationProperty(EMAIL_THREADPOOL_KEEP_ALIVE_SECONDS_ENV, EMAIL_THREADPOOL_KEEP_ALIVE_SECONDS_SYSPROP, "60"));
    }

    /**
     * @return the thread name prefix for the email thread pool
     */
    public String getEmailThreadPoolThreadNamePrefix() {
        return getConfigurationProperty(EMAIL_THREADPOOL_THREAD_NAME_PREFIX_ENV, EMAIL_THREADPOOL_THREAD_NAME_PREFIX_SYSPROP, "emailTaskExecutor-");
    }

    /**
     * Looks for the given key in the following places (in order):
     *
     * 1) Environment variables
     * 2) System Properties
     *
     * @param envKey
     * @param sysPropKey
     * @param defaultValue
     * @return the configured property value or default value if not found
     */
    private String getConfigurationProperty(String envKey, String sysPropKey, String defaultValue) {
        String value = env.getProperty(sysPropKey);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(envKey);
        }
        if (value == null || value.trim().isEmpty()) {
            value = defaultValue;
        }
        logHelper.getLogger().debug("Config Property: {}/{} = {}", envKey, sysPropKey, value);
        return value;
    }
}