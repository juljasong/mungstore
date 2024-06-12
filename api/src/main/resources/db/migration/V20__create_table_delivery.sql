CREATE TABLE DELIVERY (
    DELIVERY_ID         BIGINT          AUTO_INCREMENT PRIMARY KEY,
    SHIPPER             VARCHAR(50),
    TRACKING_NO         VARCHAR(50),
    TEL1                VARCHAR(11)     NOT NULL,
    TEL2                VARCHAR(11)     NOT NULL,
    ZIPCODE             VARCHAR(6)      NOT NULL,
    CITY                VARCHAR(20)     NOT NULL,
    STREET              VARCHAR(50)     NOT NULL,
    STATUS              VARCHAR(20)     NOT NULL,
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    BIGINT,
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE DELIVERY_HISTORY (
    LOG_ID              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    DELIVERY_ID         BIGINT,
    SHIPPER             VARCHAR(50),
    TRACKING_NO         VARCHAR(50),
    TEL1                VARCHAR(11),
    TEL2                VARCHAR(11),
    ZIPCODE             VARCHAR(6),
    CITY                VARCHAR(20),
    STREET              VARCHAR(50),
    CREATED_BY          BIGINT,
    CREATED_AT          DATETIME(6),
    LAST_MODIFIED_BY    BIGINT,
    LAST_MODIFIED_AT    DATETIME(6)
);