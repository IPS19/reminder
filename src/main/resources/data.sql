CREATE TABLE users
(
    id BIGINT PRIMARY KEY,
    user_name VARCHAR(126),
    email VARCHAR(100) UNIQUE,
    password VARCHAR(50),
    telegram_chat_id BIGINT(50),
    google_sub_number VARCHAR(256) UNIQUE,
    role       VARCHAR(32)
);

CREATE TYPE role as ENUM(
    'USER', 'ADMIN'
);

CREATE TABLE reminder
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255),
    description VARCHAR(4049),
    remind_date_time      TIMESTAMP,
    user_id     BIGINT REFERENCES users (id)
);