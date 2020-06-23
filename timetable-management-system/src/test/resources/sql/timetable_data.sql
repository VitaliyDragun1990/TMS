INSERT INTO categories (category_id, category_code, category_description) VALUES (1, 'BIO', 'Biology');
INSERT INTO categories (category_id, category_code, category_description) VALUES (2, 'HIS', 'History');

INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (1, 'Jack', 'Smith', 'Professor', CURRENT_DATE);
INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (2, 'John', 'Porter', 'Instructor', CURRENT_DATE);

INSERT INTO courses (course_id, course_name, course_description, category_id, teacher_id) VALUES (1, 'Advanced Biology', 'Amazing course', 1, 1);
INSERT INTO courses (course_id, course_name, course_description, category_id, teacher_id) VALUES (2, 'Core Biology', 'Amazing course', 1, 2);

INSERT INTO groups (group_id, group_name) VALUES (1, 'mh-10');

INSERT INTO students (student_id, s_first_name, s_last_name, enrollment_date, group_id) VALUES (1, 'Mary', 'Birkin', CURRENT_DATE, 1);
INSERT INTO students (student_id, s_first_name, s_last_name, enrollment_date, group_id) VALUES (2, 'Amanda', 'Birkin', CURRENT_DATE, 1);

INSERT INTO student_courses (student_id, course_id) VALUES (1, 1);
INSERT INTO student_courses (student_id, course_id) VALUES (1, 2);
INSERT INTO student_courses (student_id, course_id) VALUES (2, 2);

INSERT INTO classrooms (classroom_id, capacity) VALUES (1, 30);
INSERT INTO classrooms (classroom_id, capacity) VALUES (2, 60);

INSERT INTO timetables (start_date_time, duration, classroom_id, course_id)
    VALUES ('2020-03-10 09:30:00', 60, 1, 1);
INSERT INTO timetables (start_date_time, duration, classroom_id, course_id)
    VALUES ('2020-03-10 12:30:00', 60, 1, 2);
INSERT INTO timetables (start_date_time, duration, classroom_id, course_id)
    VALUES ('2020-03-25 09:30:00', 60, 1, 1);
