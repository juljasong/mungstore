ALTER TABLE PAYMENT
ADD COLUMN PAYMENT_PROVIDER VARCHAR(10);

ALTER TABLE PAYMENT
ADD CONSTRAINT UNIQUE_PAYMENT_TID UNIQUE (TID);