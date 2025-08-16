package com.fastcode.emailApi.controller;

import com.fastcode.emailApi.application.EmailService;
import com.fastcode.emailApi.application.dto.EmailMessage;
import com.fastcode.emailApi.application.EmailMapper;
import com.fastcode.emailApi.application.dto.EmailMessageAttachmentInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;
    private final EmailMapper emailMapper;

    @Autowired
    public EmailController(EmailService emailService, EmailMapper emailMapper) {
        this.emailService = emailService;
        this.emailMapper = emailMapper;
    }

    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@ModelAttribute EmailMessageAttachmentInput emailMessage) {
        try {
            emailService.sendMessage(emailMapper.toEmailMessage(emailMessage));
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Endpoint to send bulk email messages
     *
     * @param emailMessageInput List of email messages to be sent
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping(value = "/sendBulkMessage")
    public ResponseEntity<String> sendBulkMessage(@RequestBody List<EmailMessage> emailMessageInput) {
        try {
            emailService.sendMessage(emailMessageInput);
            return new ResponseEntity<>("Bulk emails have been sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send bulk emails: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * Endpoint to send bulk email messages with Parallel
     *
     * @param emailMessageInput List of email messages to be sent
     * @return ResponseEntity indicating the status of the operation
     */
    @PostMapping(value = "/sendBulkMessageWithParallel")
    public ResponseEntity<String> sendBulkMessageWithParallel(@RequestBody List<EmailMessage> emailMessageInput) {
        try {
            emailService.sendMessageInParallel(emailMessageInput);
            return new ResponseEntity<>("Bulk emails have been sent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to send bulk emails: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/track/open/{emailId}")
    public ResponseEntity<byte[]> trackOpen(@PathVariable String emailId) {
        emailService.logEmailOpen(Long.valueOf(emailId));
        byte[] pixel = new byte[]{(byte) 0xFF}; // A single byte representing a transparent pixel
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(pixel);
    }

    @GetMapping("/track/click")
    public void trackClick(@RequestParam String url, @RequestParam String emailId, HttpServletResponse response) throws IOException {
        emailService.logLinkClick(Long.valueOf(emailId), url);
        response.sendRedirect(url);
    }
}
