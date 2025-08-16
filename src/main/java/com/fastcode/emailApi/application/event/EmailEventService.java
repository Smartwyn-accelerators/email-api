package com.fastcode.emailApi.application.event;


import com.fastcode.emailApi.application.dto.EmailEventPayload;

public interface EmailEventService {
    void processEmailOpen(EmailEventPayload eventPayload);
    void processEmailClick(EmailEventPayload eventPayload);
}
