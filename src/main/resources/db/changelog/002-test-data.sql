--changeset sarzhin:1
INSERT INTO reminder.users (user_name, email, password, telegram_chat_id, google_sub_number)
VALUES ('user1', 'user1@email.com', '{noop}password1', 235689, '9865479'),
       ('user2', 'user2@email.com', '{noop}password2', 235510, '9865455'),
       ('user3', 'user3@email.com', '{noop}password3', 235511, '9865456');

--changeset sarzhin:2
INSERT INTO reminder.reminder (title, description, remind_date_time, user_id)
VALUES ('title1', 'description 1', '2026-10-12 13:05:00', (select id from users where email = 'user1@email.com')),
       ('title1', 'description 1', NOW() + INTERVAL '2' MINUTE, (select id from users where email = 'user1@email.com')),

       ('title2', 'description 2', '2026-10-12 13:05:00', (select id from users where email = 'user2@email.com')),
       ('title2', 'description 2', '2026-10-12 13:05:00', (select id from users where email = 'user2@email.com')),

       ('title3', 'description 3', '2026-10-12 13:05:00', (select id from users where email = 'user3@email.com')),
       ('title3','description 3', '2026-10-12 13:05:00', (select id from users where email = 'user3@email.com'));
