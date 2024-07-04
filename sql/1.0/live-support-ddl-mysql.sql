/* live-support begin */
DROP TABLE IF EXISTS support_chat;
CREATE TABLE support_chat
(
    id                   BIGINT(20) NOT NULL,
    party_id             BIGINT(20),
    no_login_id          VARCHAR(32),
    remarks              VARCHAR(64),
    username             VARCHAR(64),
    uid                  VARCHAR(64),
    user_unread          INT,
    user_read_time       BIGINT,
    supporter_unread     INT,
    supporter_read_time  BIGINT,
    lastmsg              VARCHAR(10240),
    last_time            BIGINT,
    blacklist           TINYINT(1) DEFAULT 0,
    PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;
CREATE  INDEX inx_support_chat_last_time ON support_chat(last_time);

DROP TABLE IF EXISTS support_message;
CREATE TABLE support_message
(
   id                   BIGINT(20) NOT NULL,
   chatid               BIGINT(20),
   party_id             BIGINT(20),
   no_login_id          VARCHAR(32),
   direction            VARCHAR(22),
   type                 VARCHAR(32),
   content              VARCHAR(10240),
   deleted              TINYINT(1) DEFAULT 0,
   created_time         BIGINT,
   PRIMARY KEY (id)
)ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

CREATE  INDEX inx_support_message_party ON support_message(party_id,created_time);
CREATE  INDEX inx_support_message_no_login_id ON support_message(no_login_id,created_time);

/* live-support end */


