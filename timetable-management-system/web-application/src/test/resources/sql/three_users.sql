INSERT INTO roles(role_id, role_name)
	VALUES (100000, 'ROLE_ADMIN'), (100001, 'ROLE_STUDENT'), (100002, 'ROLE_TEACHER');
	
INSERT INTO users(user_id, username, first_name, last_name, email, password)
	VALUES (100000, 'admin', 'Admin', 'Admin', 'admin@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
	       (100001, 'student', 'Student', 'Student', 'student@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
	       (100002, 'teacher', 'Teacher', 'Teacher', 'teacher@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO');
	
INSERT INTO user_roles(user_id, role_id)
	VALUES (100000, 100000),
	       (100001, 100001),
	       (100002, 100002);
