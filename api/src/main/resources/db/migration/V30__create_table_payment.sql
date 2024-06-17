CREATE TABLE PAYMENT (
    PAYMENT_ID          BIGINT          PRIMARY KEY AUTO_INCREMENT,
    ORDER_ID            BIGINT          NOT NULL,
    TID                 VARCHAR(50)     NOT NULL,
    APPROVAL_ID         VARCHAR(50)     NOT NULL,
    TOTAL_AMOUNT        INTEGER         NOT NULL,
    PAYMENT_METHOD      ENUM('CARD', 'MONEY')     NOT NULL,
    CARD_NO             VARCHAR(15),
    CARD_CORP           VARCHAR(10),
    INSTALL_MONTH       VARCHAR(2),
    STATUS              ENUM('COMPLETED', 'CANCELLED'),
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    BIGINT,
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);