CREATE TABLE REVISION_HISTORY (
	REVISION_ID BIGINT(19) NOT NULL AUTO_INCREMENT,
	MEMBER_ID BIGINT(19) NULL DEFAULT NULL,
	UPDATED_AT BIGINT(19) NULL DEFAULT NULL,
	PRIMARY KEY (REVISION_ID) USING BTREE
);
