package com.fastcode.emailApi.application.strategy;


import com.fastcode.emailApi.EmailApiPropertiesConfiguration;
import com.fastcode.emailApi.EmailServiceException;
import com.mailgun.api.v3.MailgunMessagesApi;
import com.mailgun.client.MailgunClient;
import com.mailgun.model.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.AddressException;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("Mailgun")
public class MailgunProviderStrategy implements ProviderStrategy {

    @Autowired
    private MailgunMessagesApi mailgunClient;

    @Autowired
    private EmailApiPropertiesConfiguration env;


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
            Message.MessageBuilder messageBuilder = Message.builder()
                    .from(from)
                    .to(splitEmails(to))
                    .subject(subject);

            if (isHtml) {
                messageBuilder.html(content);
            } else {
                messageBuilder.text(content);
            }
            // Add CC and BCC
            if (cc != null && !cc.isEmpty()) {
                messageBuilder.cc(splitEmails(cc));
            }
            if (bcc != null && !bcc.isEmpty()) {
                messageBuilder.bcc(splitEmails(bcc));
            }
            if (replyTo != null && !replyTo.isEmpty()) {
                messageBuilder.replyTo(replyTo);
            }

            // Add attachments
            if (attachments != null && !attachments.isEmpty()) {
                messageBuilder.attachment(attachments);
            }

            // Add inline images
            if (inlineImages != null && !inlineImages.isEmpty()) {
                messageBuilder.inline(inlineImages);
            }

            // Send the email
            mailgunClient.sendMessage(env.getMailgunApiDomain(), messageBuilder.build());
        } catch (Exception e) {
            throw new EmailServiceException("Failed to send email using Mailgun", e);
        }
    }

    private List<String> splitEmails(String emails) throws AddressException {
        List<String> addresses = new ArrayList<>();

        if (emails == null || emails.isEmpty()) {
            return addresses;
        }

        String[] emailArray = emails.split(",", -1);

        for (String email : emailArray) {
            addresses.add(email.trim());
        }
        return addresses;
    }


    @Override
    public String getType() {
        return "Mailgun";
    }
}
