package com.mung.api.service;

import com.mung.api.client.MailgunClient;
import com.mung.common.domain.SendMailForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final MailgunClient mailgunClient;

    public String sendEmail(SendMailForm sendMailForm) {
        return mailgunClient.sendEmail(sendMailForm).getBody();
    }
}
