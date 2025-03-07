-- Create a test user for integration tests
INSERT INTO users (username, password) VALUES 
('testuser', '$2a$10$eDhncK/4cNH2KE.Y51AWpeL8K4/rVkuJ8uSkm/A7.1gsJwWie3wQy') 
ON CONFLICT (username) DO NOTHING;

-- Add roles for the test user
INSERT INTO user_entity_roles (user_entity_id, roles) 
SELECT id, 'USER' FROM users WHERE username = 'testuser';
