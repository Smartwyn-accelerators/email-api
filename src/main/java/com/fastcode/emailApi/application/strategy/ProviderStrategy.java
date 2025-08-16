package com.fastcode.emailApi.application.strategy;


import java.io.File;
import java.util.List;
import java.util.Map;

public interface ProviderStrategy {
    void sendMessage(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml);
    void sendMessage(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml, List<File> inlineImages, List<File> attachments, Map<Long,byte[]> imageDataSourceMap);
    String getType();
}

