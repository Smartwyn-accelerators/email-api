package com.fastcode.emailApi;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

@Configuration
@ComponentScan("com.fastcode.emailApi")
@EnableJpaRepositories(basePackages = "com.fastcode.emailApi.domain.irepository")
@EntityScan(basePackages = "com.fastcode.emailApi.domain.model")
@EnableRetry
public class EnableEmailApiAutoConfiguration {

    @Autowired
    EmailApiPropertiesConfiguration env;

    @Autowired
    EmailApiLoggingHelper loggingHelper;

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        try {
            return AmazonSimpleEmailServiceClientBuilder.standard()
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .withRegion(env.getAwsRegion())
                    .build();
        } catch (Exception e) {
            loggingHelper.getLogger().error("Error while configure AWS SES credentials: {}", e.getMessage());
            return null;
        }
    }

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(env.getSendGridApiKey());
    }

    @Bean
    public MailgunMessagesApi mailgunMessagesApi() {
        return MailgunClient.config(env.getMailgunApiKey())
                .createApi(MailgunMessagesApi.class);
    }

    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(env.getRateLimitEmailsPerSecond());
    }

    @Bean
    public RetryTemplate retryTemplate() {
        return new RetryTemplateBuilder()
                .maxAttempts(env.getMaxRetryAttempts())
                .fixedBackoff(env.getRetryAttemptsInterval())
                .build();
    }


}
