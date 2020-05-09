DROP TABLE IF EXISTS student_courses;
DROP TABLE IF EXISTS timetables;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS classrooms;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS groups;
DROP SEQUENCE IF EXISTS students_student_id_seq;
DROP SEQUENCE IF EXISTS timetables_timetable_id_seq;
DROP SEQUENCE IF EXISTS courses_course_id_seq;
DROP SEQUENCE IF EXISTS teachers_teacher_id_seq;
DROP SEQUENCE IF EXISTS classrooms_classroom_id_seq;
DROP SEQUENCE IF EXISTS categories_category_id_seq;
DROP SEQUENCE IF EXISTS groups_group_id_seq;

CREATE SEQUENCE groups_group_id_seq;

CREATE TABLE groups (
    group_id integer DEFAULT nextval('groups_group_id_seq') PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL
);

CREATE SEQUENCE categories_category_id_seq;

CREATE TABLE categories (
    category_id integer DEFAULT nextval('categories_category_id_seq') PRIMARY KEY,
    category_code VARCHAR(100) NOT NULL,
    category_description VARCHAR(255)
);

CREATE SEQUENCE classrooms_classroom_id_seq;

CREATE TABLE classrooms (
    classroom_id integer DEFAULT nextval('classrooms_classroom_id_seq') PRIMARY KEY,
    capacity INTEGER NOT NULL
);

CREATE SEQUENCE teachers_teacher_id_seq;

CREATE TABLE teachers (
    teacher_id integer DEFAULT nextval('teachers_teacher_id_seq') PRIMARY KEY,
    t_first_name VARCHAR(50) NOT NULL,
    t_last_name VARCHAR(50) NOT NULL,
    title VARCHAR(50) NOT NULL,
    date_hired DATE
);

CREATE SEQUENCE courses_course_id_seq;

CREATE TABLE courses (
    course_id integer DEFAULT nextval('courses_course_id_seq') PRIMARY KEY,
    course_name VARCHAR(50) NOT NULL,
    course_description VARCHAR(255),
    category_id INTEGER NOT NULL,
    teacher_id INTEGER NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE RESTRICT
);

CREATE SEQUENCE timetables_timetable_id_seq;

CREATE TABLE timetables (
    timetable_id integer DEFAULT nextval('timetables_timetable_id_seq') PRIMARY KEY,
    start_date_time TIMESTAMP,
    duration INTEGER,
    classroom_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    teacher_id INTEGER NOT NULL,
    FOREIGN KEY (classroom_id) REFERENCES classrooms(classroom_id) ON DELETE RESTRICT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE RESTRICT
);

CREATE SEQUENCE students_student_id_seq;

CREATE TABLE students (
    student_id integer DEFAULT nextval('students_student_id_seq') PRIMARY KEY,
    s_first_name VARCHAR(50) NOT NULL,
    s_last_name VARCHAR(50) NOT NULL,
    enrollment_date DATE,
    group_id INTEGER,
    FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE RESTRICT
);

CREATE TABLE student_courses (
    student_id INTEGER NOT NULL,
    course_id INTEGER NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT
);
