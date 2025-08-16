package com.fastcode.emailApi.application.strategy;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.fastcode.emailApi.EmailServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("AmazonSES")
public class AmazonSESProviderStrategy implements ProviderStrategy {

    @Autowired
    private AmazonSimpleEmailService sesClient;

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
            // Build MIME message
            Session session = Session.getDefaultInstance(System.getProperties());
            MimeMessage mimeMessage = new MimeMessage(session);

            // set from address
            if (from != null && !from.isEmpty()) {
                mimeMessage.setFrom(new InternetAddress(from));
            }

            // set recipient addresses
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, splitEmails(to));
            if (cc != null && !cc.isEmpty()) {
                mimeMessage.setRecipients(MimeMessage.RecipientType.CC, splitEmails(cc));
            }
            if (bcc != null && !bcc.isEmpty()) {
                mimeMessage.setRecipients(MimeMessage.RecipientType.BCC, splitEmails(bcc));
            }
            // Set reply-to addresses
            if (replyTo != null && !replyTo.isEmpty()) {
                mimeMessage.setReplyTo(splitEmails(replyTo));
            }

            mimeMessage.setSubject(subject, "UTF-8");

            // Create the body part
            MimeMultipart multipart = new MimeMultipart("mixed");

            MimeBodyPart bodyPart = new MimeBodyPart();
            if (isHtml) {
                bodyPart.setContent(content, "text/html; charset=UTF-8");
            } else {
                bodyPart.setContent(content, "text/plain; charset=UTF-8");
            }
            multipart.addBodyPart(bodyPart);

            // Add inline images
            if (inlineImages != null && !inlineImages.isEmpty()) {
                for (File image : inlineImages) {
                    MimeBodyPart imagePart = new MimeBodyPart();
                    DataSource dataSource = new FileDataSource(image);
                    imagePart.setDataHandler(new DataHandler(dataSource));
                    imagePart.setHeader("Content-ID", "<" + image.getName() + ">");
                    multipart.addBodyPart(imagePart);
                }
            }

            // Add attachments
            if (attachments != null && !attachments.isEmpty()) {
                for (File attachment : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    DataSource dataSource = new FileDataSource(attachment);
                    attachmentPart.setDataHandler(new DataHandler(dataSource));
                    attachmentPart.setFileName(attachment.getName());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            // Add inline images from imageDataSourceMap
            if (imageDataSourceMap != null) {
                for (Map.Entry<Long, byte[]> entry : imageDataSourceMap.entrySet()) {
                    MimeBodyPart imagePart = new MimeBodyPart();
                    DataSource dataSource = new ByteArrayDataSource(entry.getValue(), "image/png");
                    imagePart.setDataHandler(new DataHandler(dataSource));
                    imagePart.setHeader("Content-ID", "<image" + entry.getKey() + ".png>");
                    multipart.addBodyPart(imagePart);
                }
            }

            mimeMessage.setContent(multipart);

            // Convert MIME message to raw format
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mimeMessage.writeTo(baos);
            byte[] rawMessage = baos.toByteArray();

            // Send the email using SendRawEmailRequest
            RawMessage rawMessageRequest = new RawMessage(ByteBuffer.wrap(rawMessage));
            SendRawEmailRequest sendRawEmailRequest = new SendRawEmailRequest()
                    .withRawMessage(rawMessageRequest)
                    .withSource(from);

            sesClient.sendRawEmail(sendRawEmailRequest);
        } catch (MessagingException e) {
            throw new EmailServiceException("Failed to send email due to MessagingException", e);
        } catch (Exception e) {
            throw new EmailServiceException("Failed to send email using Amazon SES", e);
        }
    }
    private Address[] splitEmails(String emails) throws AddressException {

        List<InternetAddress> validAddresses = new ArrayList<>();

        if (emails == null || emails.isEmpty()) {
            return validAddresses.toArray(new Address[0]);
        }
        String[] emailArray = emails.split(",", -1);
        for (String email : emailArray) {
            validAddresses.add(new InternetAddress(email.trim()));
        }
        return validAddresses.toArray(new Address[0]);
    }

    @Override
    public String getType() {
        return "AmazonSES";
    }
}
