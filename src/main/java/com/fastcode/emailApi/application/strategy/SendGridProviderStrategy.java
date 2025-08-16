package com.fastcode.emailApi.application.strategy;

import com.sendgrid.*;
import com.fastcode.emailApi.EmailServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Map;

@Component("SendGrid")
public class SendGridProviderStrategy implements ProviderStrategy {

    @Autowired
    private SendGrid sendGrid;

    @Override
    public void sendMessage(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml) {
        sendEmail(to, cc, bcc, from, replyTo, subject, content, isHtml, null, null, null);
    }

    @Override
    public void sendMessage(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml,
                            List<File> inlineImages, List<File> attachments, Map<Long, byte[]> imageDataSourceMap) {
        sendEmail(to, cc, bcc, from, replyTo, subject, content, isHtml, inlineImages, attachments, imageDataSourceMap);
    }

    private void sendEmail(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml,
                           List<File> inlineImages, List<File> attachments, Map<Long, byte[]> imageDataSourceMap) {
        try {
            Email fromEmail = new Email(from);
            Email toEmail = new Email(to);

            Content contentType;
            if (isHtml) {
                contentType = new Content("text/html", content);
            } else {
                contentType = new Content("text/plain", content);
            }

            Mail mail = new Mail(fromEmail, subject, toEmail, contentType);
            
            // Add CC and BCC
            if (cc != null && !cc.isEmpty()) {
                for (String ccEmail : cc.split(",", -1)) {
                    mail.personalization.get(0).addCc(new Email(ccEmail.trim()));
                }
            }
            if (bcc != null && !bcc.isEmpty()) {
                for (String bccEmail : bcc.split(",", -1)) {
                    mail.personalization.get(0).addBcc(new Email(bccEmail.trim()));
                }
            }
            if (replyTo != null && !replyTo.isEmpty()) {
                for (String replyToEmail : replyTo.split(",", -1)) {
                    mail.setReplyTo(new Email(replyToEmail));
                }
            }

            // Add inline images and attachments
            if (inlineImages != null && !inlineImages.isEmpty()) {
                for (File image : inlineImages) {
                    Attachments attachment = new Attachments();
                    attachment.setContent(new String(java.nio.file.Files.readAllBytes(image.toPath())));
                    attachment.setType("image/png");
                    attachment.setFilename(image.getName());
                    attachment.setDisposition("inline");
                    mail.addAttachments(attachment);
                }
            }

            if (attachments != null && !attachments.isEmpty()) {
                for (File attachment : attachments) {
                    Attachments attach = new Attachments();
                    attach.setContent(new String(java.nio.file.Files.readAllBytes(attachment.toPath())));
                    attach.setType("application/octet-stream");
                    attach.setFilename(attachment.getName());
                    attach.setDisposition("attachment");
                    mail.addAttachments(attach);
                }
            }

            // Send email
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (Exception e) {
            throw new EmailServiceException("Failed to send email using SendGrid", e);
        }
    }


    @Override
    public String getType() {
        return "SendGrid";
    }
}
