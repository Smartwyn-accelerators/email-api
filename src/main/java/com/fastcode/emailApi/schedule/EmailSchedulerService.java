package com.fastcode.emailApi.schedule;

import com.fastcode.scheduler.application.job.IJobAppService;
import com.fastcode.scheduler.application.job.dto.CreateJobInput;
import com.fastcode.scheduler.application.trigger.ITriggerAppService;
import com.fastcode.scheduler.application.trigger.dto.CreateTriggerInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmailSchedulerService {

    @Autowired
    private ITriggerAppService triggerAppService;

    @Autowired
    private IJobAppService jobAppService;


    public void scheduleEmail(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml,
                              List<File> inlineImages, List<File> attachments, Map<Long,byte[]> imageDataSourceMap, LocalDateTime scheduleTime, String providerName) {
        try {
            Map<String, String> jobDataMap = new HashMap<>();
            jobDataMap.put("to", to);
            jobDataMap.put("cc", cc != null ? cc : "");
            jobDataMap.put("bcc", bcc != null ? bcc : "");
            jobDataMap.put("from", from != null ? from : "");
            jobDataMap.put("replyTo", replyTo != null ? replyTo : "");
            jobDataMap.put("subject", subject);
            jobDataMap.put("content", content);
            jobDataMap.put("isHtml", String.valueOf(isHtml));
            jobDataMap.put("inlineImages", serializeFiles(inlineImages));
            jobDataMap.put("attachments", serializeFiles(attachments));
            jobDataMap.put("imageDataSourceMap", serializeImageDataSourceMap(imageDataSourceMap));
            jobDataMap.put("providerName", providerName);

            CreateJobInput createJobInput = new CreateJobInput();
            createJobInput.setJobName("emailJob_" + subject + "_" + to);
            createJobInput.setJobGroup("emailGroup");
            createJobInput.setJobClass(SendEmailJob.class.getName());
            createJobInput.setJobDescription("Job for sending email to " + to);
            createJobInput.setIsDurable(Boolean.valueOf(true));
            createJobInput.setJobMapData(jobDataMap);
            jobAppService.createJob(createJobInput);

            CreateTriggerInput input = new CreateTriggerInput();
            Date startTime = java.sql.Timestamp.valueOf(scheduleTime);
            input.setCronExpression(generateCronExpression(scheduleTime));
            input.setRepeatCount(1);
            input.setTriggerMapData(jobDataMap);
            input.setRepeatInterval(0); // Not used with Cron triggers
            input.setTriggerName("emailTrigger_" + subject + "_" + to);
            input.setTriggerGroup("emailGroup");
            input.setTriggerType("Cron");
            input.setStartTime(startTime);
            input.setEndTime(null); // Set to null for no end time
            input.setJobName("emailJob_" + subject + "_" + to);
            input.setJobGroup("emailGroup");
            input.setJobClass(SendEmailJob.class.getName()); // Assuming SendEmailJob is the class to execute
            input.setTriggerDescription("Trigger for sending email to " + to);
            triggerAppService.createTrigger(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String serializeFiles(List<File> files) {
        if (files == null || files.isEmpty()) {
            return "";
        }
        return files.stream()
                .map(File::getAbsolutePath)
                .collect(Collectors.joining(","));
    }

    private String serializeImageDataSourceMap(Map<Long, byte[]> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + Base64.getEncoder().encodeToString(entry.getValue()))
                .collect(Collectors.joining(","));
    }
    public String generateCronExpression(LocalDateTime scheduleTime) {
        // Extract values from LocalDateTime
        int second = scheduleTime.getSecond();
        int minute = scheduleTime.getMinute();
        int hour = scheduleTime.getHour();
        int dayOfMonth = scheduleTime.getDayOfMonth();
        int month = scheduleTime.getMonthValue();
        int year = scheduleTime.getYear();

        String cronExpression = String.format("%d %d %d %d %d ? %d",
                Integer.valueOf(second),
                Integer.valueOf(minute),
                Integer.valueOf(hour),
                Integer.valueOf(dayOfMonth),
                Integer.valueOf(month),
                Integer.valueOf(year));

        return cronExpression;
    }


}
