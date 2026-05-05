--changeset sarzhin:1
CREATE TABLE IF NOT EXISTS reminder.users
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_name VARCHAR(126),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(70),
    telegram_chat_id BIGINT
);

--changeset sarzhin:2
CREATE TABLE IF NOT EXISTS reminder.user_role(
    user_id  BIGINT REFERENCES reminder.users(id) ON DELETE CASCADE,
    role VARCHAR(20),
    PRIMARY KEY (user_id, role)
);

--changeset sarzhin:3
CREATE TABLE IF NOT EXISTS reminder.reminder
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title       VARCHAR(255),
    description VARCHAR(4049),
    remind_date_time      TIMESTAMP,
    user_id     BIGINT REFERENCES reminder.users (id)
);