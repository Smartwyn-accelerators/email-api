package com.fastcode.emailApi.application;

import com.fastcode.emailApi.application.dto.EmailMessage;

import java.util.List;

public interface EmailService {
    void sendMessage(EmailMessage emailMessage);
    void sendMessage(List<EmailMessage> emailMessages);
    void sendMessageInParallel(List<EmailMessage> emailMessages);
    void logEmailOpen(Long emailId);
    void logLinkClick(Long emailId, String url);
}
