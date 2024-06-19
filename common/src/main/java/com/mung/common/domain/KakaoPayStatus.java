package com.mung.common.domain;

public enum KakaoPayStatus {
    READY,
    SEND_TMS,
    OPEN_PAYMENT,
    SELECT_METHOD,
    ARS_WAITING,
    AUTH_PASSWORD,

    ISSUED_SID,
    SUCCESS_PAYMENT,

    PART_CANCEL_PAYMENT,
    CANCEL_PAYMENT,

    FAIL_AUTH_PASSWORD,
    QUIT_PAYMENT,
    FAIL_PAYMENT
}
