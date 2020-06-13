-- Classrooms
INSERT INTO classrooms (classroom_id, capacity) VALUES (1, 30);

-- Groups
INSERT INTO groups (group_id, group_name) VALUES (1, 'mh-10');

-- Categories
INSERT INTO categories (category_id, category_code, category_description)
VALUES (1, 'ART', 'Art');

-- Teachers
INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (1000, 'Jack', 'Smith', 'Professor', '2020-04-20');

-- Courses
INSERT INTO courses (course_id, course_name, course_description, category_id, teacher_id) 
	VALUES (1000, 'Advanced Art', 'Amazing course', 1, 1000);
	
-- Students
INSERT INTO students (student_id, s_first_name, s_last_name, enrollment_date, group_id) VALUES (1000, 'John', 'Doe', '2020-04-30', 1);

-- Timetables
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id, teacher_id)
    VALUES (1000, '2020-05-01 09:30:00', 60, 1, 1000, 1000); -- Art
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id, teacher_id)
    VALUES (1001, '2020-05-05 09:30:00', 60, 1, 1000, 1000); -- Art
INSERT INTO timetables (timetable_id, start_date_time, duration, classroom_id, course_id, teacher_id)
    VALUES (1002, '2020-05-07 09:30:00', 60, 1, 1000, 1000); -- Art
