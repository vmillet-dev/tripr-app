INSERT INTO users (id, username, password) VALUES 
(1, 'testuser', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'); -- password: 'password'

INSERT INTO users (id, username, password) VALUES 
(2, 'adminuser', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG'); -- password: 'password'

INSERT INTO user_entity_roles (user_entity_id, roles) VALUES 
(1, 'USER');

INSERT INTO user_entity_roles (user_entity_id, roles) VALUES 
(2, 'USER'),
(2, 'ADMIN');

ALTER SEQUENCE users_id_seq RESTART WITH 3;
