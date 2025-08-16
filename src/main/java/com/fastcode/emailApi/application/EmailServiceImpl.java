package com.fastcode.emailApi.application;

import com.fastcode.emailApi.EmailApiPropertiesConfiguration;
import com.fastcode.emailApi.EmailServiceException;
import com.fastcode.emailApi.application.dto.EmailMessage;
import com.fastcode.emailApi.application.strategy.ProviderStrategy;
import com.fastcode.emailApi.domain.irepository.EmailTrackingRepository;
import com.fastcode.emailApi.domain.model.EmailTracking;
import com.fastcode.emailApi.schedule.EmailSchedulerService;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private Map<String, ProviderStrategy> providerStrategies;

    @Autowired
    private EmailSchedulerService schedulerService;

    @Autowired
    private EmailApiPropertiesConfiguration env;

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private EmailTrackingRepository emailTrackingRepository;


    @Override
    public void sendMessage(EmailMessage emailMessage) {

        String strategyName = (emailMessage.getDefaultEmailProvider() == null || emailMessage.getDefaultEmailProvider().isEmpty())
                ? env.getDefaultEmailProvider()
                : emailMessage.getDefaultEmailProvider();

        String fromAddress = (emailMessage.getFrom() == null || emailMessage.getFrom().isEmpty())
                ? env.getFromAddress()
                : emailMessage.getFrom();

        if (emailMessage.getScheduledTime() != null) {
            schedulerService.scheduleEmail(emailMessage.getTo(), emailMessage.getCc(), emailMessage.getBcc(), fromAddress, emailMessage.getReplyTo(), emailMessage.getSubject(), emailMessage.getContent(), emailMessage.isHtml(), emailMessage.getInlineImages(), emailMessage.getAttachments(), emailMessage.getImageDataSourceMap(), emailMessage.getScheduledTime(), strategyName);
        }
        else {
            ProviderStrategy strategy = providerStrategies.get(strategyName);
            EmailTracking emailTracking = saveEmailTracking(emailMessage);
            String trackingUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/api/email/track/open/" + emailTracking.getId();
            emailMessage.setContent(addTrackingPixel(emailMessage.getContent(), trackingUrl));
            retryTemplate.execute(context -> {
                try {
                    strategy.sendMessage(emailMessage.getTo(), emailMessage.getCc(), emailMessage.getBcc(), fromAddress, emailMessage.getReplyTo(), emailMessage.getSubject(), emailMessage.getContent(), emailMessage.isHtml(), emailMessage.getInlineImages(), emailMessage.getAttachments(), emailMessage.getImageDataSourceMap());
                } catch (EmailServiceException e) {
                    throw e;
                }
                return null;
            });
        }

    }

    private EmailTracking saveEmailTracking(EmailMessage emailMessage) {
        EmailTracking emailTracking = new EmailTracking();
        emailTracking.setTo(emailMessage.getTo());
        emailTracking.setSubject(emailMessage.getSubject());
        emailTracking.setBody(emailMessage.getContent());
        return emailTrackingRepository.save(emailTracking);
    }
    private String addTrackingPixel(String content, String trackingUrl) {
        String trackingPixel = "<img src=\"" + trackingUrl + "\" width=\"1\" height=\"1\" style=\"display:none;\" />";
        return content + trackingPixel;
    }

    @Override
    public void sendMessage(List<EmailMessage> emailMessages) {
        for (EmailMessage email : emailMessages) {
            rateLimiter.acquire();
            sendMessage(email);
        }
    }

    @Override
    public void sendMessageInParallel(List<EmailMessage> emailMessages) {
        for (EmailMessage email : emailMessages) {
            rateLimiter.acquire();
            sendParallelEmail(email);
        }
    }

    @Override
    public void logEmailOpen(Long emailId) {
        Optional<EmailTracking> foundEmail = emailTrackingRepository.findById(emailId);
        if (foundEmail.isPresent()){
            EmailTracking emailTracking = foundEmail.get();
            emailTracking.setIsOpen(true);
            emailTrackingRepository.save(emailTracking);
        }
    }

    @Override
    public void logLinkClick(Long emailId, String url) {
        Optional<EmailTracking> foundEmail = emailTrackingRepository.findById(emailId);
        if (foundEmail.isPresent()){
            EmailTracking emailTracking = foundEmail.get();
            emailTracking.setIsClick(true);
            emailTrackingRepository.save(emailTracking);
        }
    }


    @Async("emailTaskExecutor")
    public void sendParallelEmail(EmailMessage emailMessageInput) {
        sendMessage(emailMessageInput);
    }
}
