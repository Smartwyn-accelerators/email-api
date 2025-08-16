package com.fastcode.emailApi.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fastcode.emailApi.application.dto.EmailMessage;
import com.fastcode.emailApi.application.dto.EmailMessageAttachmentInput;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class EmailMapper {

    // Method to map a single EmailMessageInput to EmailMessage
    public EmailMessage toEmailMessage(EmailMessageAttachmentInput input) throws IOException {
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setTo(input.getTo());
        emailMessage.setCc(input.getCc());
        emailMessage.setBcc(input.getBcc());
        emailMessage.setFrom(input.getFrom());
        emailMessage.setReplyTo(input.getReplyTo());
        emailMessage.setSubject(input.getSubject());
        emailMessage.setContent(input.getContent());
        emailMessage.setHtml(input.isHtml());
        emailMessage.setInlineImages(convertMultipartFilesToFiles(input.getInlineImages()));
        emailMessage.setAttachments(convertMultipartFilesToFiles(input.getAttachments()));
        emailMessage.setScheduledTime(input.getScheduledTime());
        emailMessage.setDefaultEmailProvider(input.getDefaultEmailProvider());
        emailMessage.setImageDataSourceMap(input.getImageDataSourceMap());

        return emailMessage;
    }

    // Method to map a list of EmailMessageInput to a list of EmailMessage
    public List<EmailMessage> toEmailMessages(List<EmailMessageAttachmentInput> inputs) throws IOException {
        return inputs.stream()
                .map(input -> {
                    try {
                        return toEmailMessage(input);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to map EmailMessageInput to EmailMessage: " + e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    // Method to map a list of EmailMessageInput to a list of EmailMessage
    public List<EmailMessage> toEmailMessages(List<EmailMessageAttachmentInput> inputs, List<MultipartFile> files) throws IOException {
        return inputs.stream()
                .map(input -> {
                    try {
                        return toEmailMessage(input);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to map EmailMessageInput to EmailMessage: " + e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    // Helper method to convert List<MultipartFile> to List<File>
    public List<File> convertMultipartFilesToFiles(List<MultipartFile> multipartFiles) throws IOException {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }

        List<File> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            File tempFile = convertMultipartFileToFile(multipartFile);
            if (tempFile == null) continue;
            files.add(tempFile);
        }
        return files;
    }

    public File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        File tempFile = null;
        try {
            tempFile = File.createTempFile(UUID.randomUUID().toString(), "_" + multipartFile.getOriginalFilename());
            multipartFile.transferTo(tempFile);
        } catch (IOException e) {
            System.err.println("Failed to convert multipart file to file: " + multipartFile.getOriginalFilename() + ". Error: " + e.getMessage());
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete(); // Clean up if file was created
            }
            throw e;
        }
        return tempFile;
    }
}
