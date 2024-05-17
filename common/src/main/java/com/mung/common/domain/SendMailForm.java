package com.mung.common.domain;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class SendMailForm {
    private String from;
    private String to;
    private String subject;
    private String text;
}


