DROP TABLE IF EXISTS student_courses;
DROP TABLE IF EXISTS timetables;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS teachers;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS classrooms;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS groups;


CREATE TABLE groups (
	group_id SERIAL,
	group_name VARCHAR(50) NOT NULL,
	PRIMARY KEY (group_id)
);

CREATE TABLE categories (
	category_id SERIAL,
	category_name VARCHAR(100) NOT NULL,
	category_description VARCHAR(255),
	PRIMARY KEY (category_id)
);

CREATE TABLE classrooms (
	classroom_id SERIAL,
	capacity INTEGER NOT NULL,
	PRIMARY KEY (classroom_id)
);

CREATE TABLE teachers (
	teacher_id SERIAL,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	title VARCHAR(50) NOT NULL,
	date_hired DATE,
	PRIMARY KEY (teacher_id)
);

CREATE TABLE courses (
	course_id SERIAL,
	course_name VARCHAR(50) NOT NULL,
	course_description VARCHAR(255),
	category_id INTEGER NOT NULL,
	teacher_id INTEGER NOT NULL,
	PRIMARY KEY (course_id),
	FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
	FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE RESTRICT
);

CREATE TABLE timetables (
	timetable_id SERIAL,
	start_date_time TIMESTAMP,
	duration INTEGER,
	classroom_id INTEGER NOT NULL,
	course_id INTEGER NOT NULL,
	teacher_id INTEGER NOT NULL,
	PRIMARY KEY (timetable_id),
	FOREIGN KEY (classroom_id) REFERENCES classrooms(classroom_id) ON DELETE RESTRICT,
	FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT,
	FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE RESTRICT
);

CREATE TABLE students (
	student_id SERIAL,
	first_name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	enrollment_date DATE,
	group_id INTEGER,
	PRIMARY KEY (student_id),
	FOREIGN KEY (group_id) REFERENCES groups(group_id) ON DELETE RESTRICT
);

CREATE TABLE student_courses (
	student_id INTEGER NOT NULL,
	course_id INTEGER NOT NULL,
	PRIMARY KEY (student_id, course_id),
	FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE RESTRICT,
	FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE RESTRICT
);
