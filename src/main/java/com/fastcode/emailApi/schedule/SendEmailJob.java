package com.fastcode.emailApi.schedule;

import com.fastcode.emailApi.EmailServiceException;
import com.fastcode.emailApi.application.strategy.ProviderStrategy;
import com.fastcode.emailApi.domain.irepository.EmailTrackingRepository;
import com.fastcode.emailApi.domain.model.EmailTracking;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SendEmailJob extends QuartzJobBean {

    @Autowired
    private Map<String, ProviderStrategy> providerStrategies;

    @Autowired
    private RetryTemplate retryTemplate;


    @Autowired
    private EmailTrackingRepository emailTrackingRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Map<String, Object> jobDataMap = context.getMergedJobDataMap();
        String to = (String) jobDataMap.get("to");
        String cc = (String) jobDataMap.get("cc");
        String bcc = (String) jobDataMap.get("bcc");
        String from = (String) jobDataMap.get("from");
        String replyTo = (String) jobDataMap.get("replyTo");
        String subject = (String) jobDataMap.get("subject");
        String content = (String) jobDataMap.get("content");
        String providerName = (String) jobDataMap.get("providerName");

        boolean isHtml = Boolean.parseBoolean((String) jobDataMap.get("isHtml"));
        List<File> inlineImages = deserializeFiles((String) jobDataMap.get("inlineImages"));
        List<File> attachments = deserializeFiles((String) jobDataMap.get("attachments"));
        Map<Long, byte[]> imageDataSourceMap = deserializeImageDataSourceMap((String) jobDataMap.get("imageDataSourceMap"));

        try {
            ProviderStrategy strategy = providerStrategies.get(providerName);
            EmailTracking emailTracking = saveEmailTracking(to,subject,content);
            String trackingUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString() + "/api/email/track/open/" + emailTracking.getId();
            String updatedContent = addTrackingPixel(content, trackingUrl);
            retryTemplate.execute(retryContext -> {
                try {
                    strategy.sendMessage(to,cc,bcc,from,replyTo,subject,updatedContent,isHtml,inlineImages,attachments,imageDataSourceMap);
                } catch (EmailServiceException e) {
                    throw e; // Retry will occur on this exception
                }
                return null;
            });
        } catch (Exception e) {
            throw new JobExecutionException("Failed to send email", e);
        }
    }
    private List<File> deserializeFiles(String serializedFiles) {
        if (serializedFiles == null || serializedFiles.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(serializedFiles.split(","))
                .map(File::new)
                .collect(Collectors.toList());
    }
    private Map<Long, byte[]> deserializeImageDataSourceMap(String serializedMap) {
        if (serializedMap == null || serializedMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(serializedMap.split(","))
                .map(entry -> entry.split(":"))
                .collect(Collectors.toMap(
                        parts -> Long.parseLong(parts[0]),
                        parts -> Base64.getDecoder().decode(parts[1])
                ));
    }

    private EmailTracking saveEmailTracking(String to, String subject, String content) {
        EmailTracking emailTracking = new EmailTracking();
        emailTracking.setTo(to);
        emailTracking.setSubject(subject);
        emailTracking.setBody(content);
        return emailTrackingRepository.save(emailTracking);
    }
    private String addTrackingPixel(String content, String trackingUrl) {
        String trackingPixel = "<img src=\"" + trackingUrl + "\" width=\"1\" height=\"1\" style=\"display:none;\" />";
        return content + trackingPixel;
    }

}
