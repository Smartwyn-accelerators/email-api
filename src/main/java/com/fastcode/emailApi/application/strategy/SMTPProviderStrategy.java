package com.fastcode.emailApi.application.strategy;

import com.fastcode.emailApi.EmailServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component("SMTP")
public class SMTPProviderStrategy implements ProviderStrategy {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendMessage(String to, String cc, String bcc, String from, String replyTo, String subject, String content,boolean isHtml) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            buildMimeMessageHelper(to, cc, bcc, from, replyTo, subject, content, isHtml, message);
        } catch (MessagingException ex) {
            throw new EmailServiceException("Failed to send email with attachment", ex);
        }

        javaMailSender.send(message);

    }

    @Override
    public void sendMessage(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml,
                            List<File> inlineImages, List<File> attachments, Map<Long,byte[]> imageDataSourceMap) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = buildMimeMessageHelper(to, cc, bcc, from, replyTo, subject, content, isHtml, message);

            if (imageDataSourceMap != null) {
                for (Map.Entry<Long, byte[]> entry : imageDataSourceMap.entrySet()) {
                    String imageName = "image" + entry.getKey() + ".png";
                    helper.addInline(imageName, new ByteArrayDataSource(entry.getValue(), "image/png"));
                }
            }

            if (inlineImages != null) {
                for (File file : inlineImages) {
                    try {
                        String imageName = file.getName(); // Use the file name as the image ID
                        helper.addInline(imageName, file);
                    } catch (MessagingException e) {
                        throw new RuntimeException("Failed to add inline image", e);
                    }
                }
            }

            if (attachments != null) {
                for (File file : attachments) {
                    try {
                        helper.addAttachment(file.getName(), file);
                    } catch (MessagingException e) {
                        throw new RuntimeException("Failed to add attachment", e);
                    }
                }
            }

        } catch (MessagingException ex) {
            throw new EmailServiceException("Failed to send email with attachment", ex);
        }

        javaMailSender.send(message);

    }

    private static MimeMessageHelper buildMimeMessageHelper(String to, String cc, String bcc, String from, String replyTo, String subject, String content, boolean isHtml, MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        if (from != null && !from.isEmpty()) {
            helper.setFrom(from);
        }

        if (replyTo != null && !replyTo.isEmpty()) {
            helper.setReplyTo(replyTo);
        }


        String[] toArray = to.split(",", -1);
        helper.setTo(toArray);

        if (cc != null && !cc.isEmpty()) {
            String[] ccArray = cc.split(",", -1);
            helper.setCc(ccArray);
        }
        if (bcc != null && !bcc.isEmpty()) {
            String[] bccArray = bcc.split(",", -1);
            helper.setBcc(bccArray);
        }

        helper.setSubject(subject);
        helper.setText(content, isHtml);

        return helper;
    }

    @Override
    public String getType() {
        return "SMTP";
    }
}

