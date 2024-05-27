CREATE TABLE MEMBER (
    MEMBER_ID           BIGINT          AUTO_INCREMENT PRIMARY KEY,
    EMAIL               VARCHAR(50)     NOT NULL UNIQUE,
    PASSWORD            VARCHAR(100)    NOT NULL,
    NAME                VARCHAR(20)     NOT NULL,
    ROLE                ENUM('USER', 'COMP', 'ADMIN') NOT NULL,
    TEL                 VARCHAR(11),
    ZIPCODE             VARCHAR(6),
    CITY                VARCHAR(20),
    STREET              VARCHAR(50),
    LOGIN_FAIL_COUNT    BIT             NOT NULL DEFAULT 0,
    IS_LOCKED           BIT             NOT NULL DEFAULT 0,
    CREATED_BY          VARCHAR(50),
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    VARCHAR(50),
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE MEMBER_LOG (
    LOG_ID              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    MEMBER_ID           BIGINT          NOT NULL,
    EMAIL               VARCHAR(50)     NOT NULL,
    PASSWORD            VARCHAR(100)    NOT NULL,
    NAME                VARCHAR(20)     NOT NULL,
    ROLE                VARCHAR(10)     NOT NULL,
    TEL                 VARCHAR(11),
    ZIPCODE             VARCHAR(5),
    CITY                VARCHAR(20),
    STREET              VARCHAR(50),
    LOGIN_FAIL_COUNT    INTEGER         NOT NULL,
    IS_LOCKED           BIT             NOT NULL,
    CREATED_BY          VARCHAR(50),
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    VARCHAR(50),
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE LOGIN_LOG (
    LOG_ID              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    EMAIL               VARCHAR(50)     NOT NULL,
    IS_SUCCESS          BIT             NOT NULL,
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE PRODUCT_CATEGORY (
    PRODUCT_CATEGORY_ID BIGINT          AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_ID          BIGINT          NOT NULL,
    CATEGORY_ID         BIGINT          NOT NULL,
    UNIQUE(PRODUCT_ID, CATEGORY_ID)
);

CREATE TABLE PRODUCT (
    PRODUCT_ID          BIGINT          AUTO_INCREMENT PRIMARY KEY,
    NAME                VARCHAR(100)    NOT NULL,
    DETAILS             LONGTEXT,
    PRICE               INTEGER         NOT NULL,
    COMP_ID             BIGINT,
    ACTIVE_FOR_SALE     BIT             NOT NULL DEFAULT 1,
    CREATED_BY          VARCHAR(50),
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    VARCHAR(50),
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE PRODUCT_LOG (
    LOG_ID              BIGINT          AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_ID          BIGINT,
    NAME                VARCHAR(100)    NOT NULL,
    DETAILS             LONGTEXT,
    PRICE               INTEGER         NOT NULL,
    COMP_ID             BIGINT,
    ACTIVE_FOR_SALE     BIT             NOT NULL,
    CREATED_BY          VARCHAR(50),
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    VARCHAR(50),
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);

CREATE TABLE OPTIONS (
    OPTION_ID           BIGINT          AUTO_INCREMENT PRIMARY KEY,
    PRODUCT_ID          BIGINT          NOT NULL,
    NAME                VARCHAR(50)     NOT NULL,
    PRICE               INTEGER
);

CREATE TABLE CATEGORY (
    CATEGORY_ID         BIGINT          AUTO_INCREMENT PRIMARY KEY,
    PARENT_ID           BIGINT,
    NAME                VARCHAR(10)     NOT NULL,
    CREATED_BY          VARCHAR(50),
    CREATED_AT          DATETIME(6)     NOT NULL,
    LAST_MODIFIED_BY    VARCHAR(50),
    LAST_MODIFIED_AT    DATETIME(6)     NOT NULL
);
