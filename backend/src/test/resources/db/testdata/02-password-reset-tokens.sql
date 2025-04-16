INSERT INTO password_reset_tokens (id, user_id, token, expiry_date, used)
VALUES (1, 1, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', CURRENT_TIMESTAMP + INTERVAL '1 day', false);

INSERT INTO password_reset_tokens (id, user_id, token, expiry_date, used)
VALUES (2, 1, 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', CURRENT_TIMESTAMP - INTERVAL '1 day', false);

INSERT INTO password_reset_tokens (id, user_id, token, expiry_date, used)
VALUES (3, 1, 'cccccccc-cccc-cccc-cccc-cccccccccccc', CURRENT_TIMESTAMP + INTERVAL '1 day', true);
