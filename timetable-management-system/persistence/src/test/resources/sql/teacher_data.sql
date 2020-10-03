INSERT INTO categories (category_id, category_code, category_description) VALUES (1, 'BIO', 'Biology');
INSERT INTO categories (category_id, category_code, category_description) VALUES (2, 'HIS', 'History');

INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (1, 'Jack', 'Smith', 'Professor', CURRENT_DATE);
INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (2, 'Anna', 'Smith', 'Instructor', CURRENT_DATE);
INSERT INTO teachers (teacher_id, t_first_name, t_last_name, title, date_hired) VALUES (3, 'John', 'Porter', 'Professor', CURRENT_DATE);

INSERT INTO courses (course_name, course_description, category_id, teacher_id) VALUES ('Advanced Biology', 'Amazing course', 1, 1);
INSERT INTO courses (course_name, course_description, category_id, teacher_id) VALUES ('Intermediate Biology', 'Amazing course', 1, 1);
INSERT INTO courses (course_name, course_description, category_id, teacher_id) VALUES ('Core History', 'Amazing course', 2, 2);
