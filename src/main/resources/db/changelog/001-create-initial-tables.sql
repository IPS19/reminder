--changeset sarzhin:1
CREATE TABLE IF NOT EXISTS reminder.users
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_name VARCHAR(126),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(50),
    telegram_chat_id BIGINT,
    google_sub_number VARCHAR(256) UNIQUE,
    role       VARCHAR(32)
);

--changeset sarzhin:2
CREATE TABLE IF NOT EXISTS reminder.reminder
(
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title       VARCHAR(255),
    description VARCHAR(4049),
    remind_date_time      TIMESTAMP,
    user_id     BIGINT REFERENCES reminder.users (id)
);