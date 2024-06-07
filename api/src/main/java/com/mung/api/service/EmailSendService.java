package com.mung.api.service;

import com.mung.api.client.MailgunClient;
import com.mung.common.domain.SendMailForm;
import com.mung.common.exception.CommonException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSendService {

    private final MailgunClient mailgunClient;

    public void sendEmail(SendMailForm sendMailForm) {
        String result = null;
        try {
            result = mailgunClient.sendEmail(sendMailForm).getBody();
        } catch (FeignException e) {
            log.error("Sending email was failed. :: ", e);

            throw new CommonException("Sending email was failed.") {
                @Override
                public int getStatusCode() {
                    return HttpStatus.INTERNAL_SERVER_ERROR.value();
                }
            };
        }
        log.info("Sending email is success. :: {}", result);
    }
}
