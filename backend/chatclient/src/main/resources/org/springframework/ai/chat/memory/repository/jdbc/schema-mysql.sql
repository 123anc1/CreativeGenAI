CREATE TABLE IF NOT EXISTS spring_ai_chat_memory
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id VARCHAR(256) NOT NULL,
    content         LONGTEXT     NOT NULL,
    type            VARCHAR(100) NOT NULL,
    timestamp       TIMESTAMP    NOT NULL,
    CONSTRAINT chk_message_type CHECK (`type` IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;