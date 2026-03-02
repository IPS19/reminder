CREATE SCHEMA reminder;

CREATE TABLE reminder.users
(
    id BIGINT PRIMARY KEY,
    user_name VARCHAR(126),
    role       VARCHAR(32)
);

CREATE TYPE reminder.role as ENUM(
    'USER', 'ADMIN'
);

CREATE TABLE reminder.reminder
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255),
    description VARCHAR(4049),
    remind      TIMESTAMP,
    user_id     BIGINT REFERENCES users (id)
);