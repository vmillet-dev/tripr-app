-- Initialize test database
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create a test user for authentication tests
INSERT INTO users (username, password) VALUES 
('testuser', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K4/rVkuJ8uSkm/A7.1gsJwWie3wQy')
ON CONFLICT DO NOTHING;

-- Add roles for the test user
INSERT INTO user_entity_roles (user_entity_id, roles) VALUES 
(1, 'USER')
ON CONFLICT DO NOTHING;
