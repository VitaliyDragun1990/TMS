-- Classrooms
INSERT INTO classrooms (classroom_id, capacity) VALUES (1, 30);

-- Groups
INSERT INTO groups (group_id, group_name) VALUES (1, 'mh-10');

-- Categories
INSERT INTO categories (category_id, category_code, category_description)
VALUES (1, 'ART', 'Art'), (2, 'HIS', 'History');

-- Teachers
INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (1000, 'Jack', 'Smith', 'Professor', '2020-04-20');
INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (1001, 'Anna', 'Smith', 'Professor', '2020-04-22');

-- Courses
INSERT INTO courses (course_id, course_name, course_description, category_id, teacher_id) 
	VALUES (1000, 'Advanced Art', 'Amazing course', 1, 1000);
INSERT INTO courses (course_id, course_name, course_description, category_id, teacher_id)
	 VALUES (1001, 'Advanced History', 'Amazing course', 2, 1001);

-- Students
INSERT INTO students (student_id, s_first_name, s_last_name, enrollment_date, group_id) VALUES (1000, 'John', 'Doe', '2020-04-30', 1);
INSERT INTO students (student_id, s_first_name, s_last_name, enrollment_date, group_id) VALUES (1001, 'Amanda', 'Doe', '2020-04-30', 1);

-- Student courses
--INSERT INTO student_courses (student_id, course_id) VALUES (1000, 1000); -- Art for John
INSERT INTO student_courses (student_id, course_id) VALUES (1000, 1000); -- Art for John
INSERT INTO student_courses (student_id, course_id) VALUES (1001, 1001); -- History for Amanda

-- Timetables
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id)
    VALUES (1000, '2020-05-01 09:30:00', 60, 1, 1000); -- Art
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id)
    VALUES (1001, '2020-05-05 09:30:00', 60, 1, 1000); -- Art
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id)
    VALUES (1002, '2020-05-01 12:30:00', 60, 1, 1001); -- History
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id)
    VALUES (1003, '2020-05-05 12:30:00', 60, 1, 1001); -- History
    
-- Roles
INSERT INTO roles(role_id, role_name) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_STUDENT'), (3, 'ROLE_TEACHER');

-- Users
INSERT INTO users(user_id, username, first_name, last_name, email, password)
	VALUES (1000, 'admin', 'Admin', 'Admin', 'admin@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
	       (1001, 'student', 'Student', 'Student', 'student@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO'),
	       (1002, 'teacher', 'Teacher', 'Teacher', 'teacher@gmail.com', '$2y$04$bwzG1HjLiH5hVJtEaS3Wz.HNx4a6fbLeO0txIVpcNytdqaIFaeETO');
	       
-- User roles
INSERT INTO user_roles(user_id, role_id)
	VALUES (1000, 1),
	       (1001, 2),
	       (1002, 3);	  
