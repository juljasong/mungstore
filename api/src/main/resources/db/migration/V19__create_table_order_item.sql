CREATE TABLE ORDER_ITEM (
    ORDER_ITEM_ID       BIGINT          AUTO_INCREMENT PRIMARY KEY,
    ORDER_ID            BIGINT          NOT NULL,
    PRODUCT_ID          BIGINT          NOT NULL,
    STOCK_ID            BIGINT          NOT NULL,
    OPTION_ID           BIGINT          NOT NULL,
    MEMBER_ID           BIGINT          NOT NULL,
    ORDER_PRICE         INTEGER         NOT NULL,
    QUANTITY            INTEGER         NOT NULL,
    CONTENTS            VARCHAR(300),
    STATUS              VARCHAR(20)     NOT NULL,
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    BIGINT,
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE ORDER_ITEM_HISTORY (
    LOG_ID              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    ORDER_ITEM_ID       BIGINT,
    ORDER_ID            BIGINT,
    PRODUCT_ID          BIGINT,
    STOCK_ID            BIGINT,
    OPTION_ID           BIGINT,
    MEMBER_ID           BIGINT,
    ORDER_PRICE         INTEGER,
    QUANTITY            INTEGER,
    CONTENTS            VARCHAR(300),
    STATUS              VARCHAR(20),
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME(6),
    LAST_MODIFIED_BY    BIGINT,
    LAST_MODIFIED_AT    DATETIME(6)
);