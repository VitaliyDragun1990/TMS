INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

INSERT INTO USERS (username, first_name, last_name, email, password)
VALUES 
    ('admin', 'Admin', 'Superuser', 'admin@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
    ('user', 'User', 'Plainuser', 'user@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO');
    
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1), (2, 2);
