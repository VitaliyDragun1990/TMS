INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'), ('ROLE_STUDENT'), ('ROLE_TEACHER');

-- Admin user: admin@gmail.com/password
-- Student user: student@gmail.com/password
-- Teacher user: teacher@gmail.com/password
INSERT INTO USERS (username, first_name, last_name, email, password)
VALUES 
    ('admin', 'Admin', 'Administrator', 'admin@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
    ('student', 'Student', 'Student', 'student@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
    ('teacher', 'Teacher', 'Teacher', 'teacher@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO');
    
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1), (2, 2), (3, 3);
