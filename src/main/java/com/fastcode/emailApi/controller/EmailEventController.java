package com.fastcode.emailApi.controller;

import com.fastcode.emailApi.application.dto.EmailEventPayload;
import com.fastcode.emailApi.application.event.EmailEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
public class EmailEventController {

    @Autowired private EmailEventService emailEventService;

    @PostMapping("/email/open")
    public ResponseEntity<Void> handleEmailOpen(@RequestBody EmailEventPayload payload) {
        emailEventService.processEmailOpen(payload);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email/click")
    public ResponseEntity<Void> handleEmailClick(@RequestBody EmailEventPayload payload) {
        emailEventService.processEmailClick(payload);
        return ResponseEntity.ok().build();
    }
}
