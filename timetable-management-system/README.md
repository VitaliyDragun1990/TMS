# Timetable Management System (TMS)
Simplistic timetable management system for schools/colleges. Accounting students, teachers, courses, student groups,
subject categories, and timetable items both for students and teachers. Allows users to manage students, teachers, courses 
and timetables, search for timetables for teachers and students for particular day or for the whole month. Users can
register themselves in the application using following roles: Admin, Teacher, and Student.

## TMS user roles

### Admin (user with the role "admin")
- Can register/update/delete student records
- Can register new teacher records
- Can register new course records
- Can register/update/delete timetable records both for students and teachers
- Can view all the available student/teacher/course/timetable records
- Can search for timetables for both students and teachers

### Teacher (user with the role "teacher")
- Can view all the available student/teacher/course/timetable records
- Can search for timetables for both students and teachers

### Student (user with the role "student")
- Can view all the available student/teacher/course/timetable records
- Can search for timetables for any student

### Anonymous (unauthenticated user)
- Can view all the courses available

## Application structure

Provided application is a simple Gradle multi-module project with two main sub-projects, each of which is a standalone application
of its own.
- First such sub-project is the 'rest-api-application'. It provides REST endpoints and caters for any clients
which can consume data using such protocol (Angular/React/Vue/etc.) It also provides complete swagger documentation.
- Next one is the 'web-application' - it provides similar functionality to the 
previous one, but also has a UI layer made using Thymeleaf technology.

## Usage

### Prerequisites
In order to build and run the application you must have the latest revision of JDK 1.8 or later version installed on your machine.

### Building the project
In order to build the whole project, open the project main directory 'timetable-management-system',
which contains all the sub-projects, in your terminal window and type in the following command and hit "Enter" afterwards:
````
gradlew build
````
This command builds the whole project and creates executable jars both for the 'web' and 'rest' applications.

### Running the application
In order to run either of the two main applications you can use gradle, or you can run the executable jar files directly:
- Open the application main directory (wep-application or rest-api-application) in your terminal window
- In case of gradle, type in the following command and press "Enter" afterwards:
###### For Unix-like systems
````
../gradlew bootRun
````
###### For Windows
````
..\gradlew bootRun
````
- In case you want to run application jar file directly, type in the following command:
###### For Unix-like systems
````
java -jar build/libs/web-application-1.0.jar
````
or
````
java -jar build/libs/rest-api-application-1.0.jar
````
###### For Windows
````
java -jar build\libs\web-application-1.0.jar
````
or
````
java -jar build\libs\rest-api-application-1.0.jar
````

### Accessing the running application

- Already running web-based application can be accessed via a web browser on the following URL:
````
http://localhost:8081
````
- Already running REST-based application has a thorough Swagger API documentation which can be accessed via a web browser
on the following URL:
###### Swagger UI
````
http://localhost:8080/v1/swagger-ui
````
###### Swagger API docs
````
http://localhost:8080/v1/api-docs
````

- On the application startup three users with different roles/privileges will be automatically created. You can log in into
the web-based application or get an access token from the REST-based one using following credentials:
````
User with 'admin' privileges: admin@gmail.com/password
User with 'teacher' privileges: teacher@gmail.com/password
User with 'student' privileges: student@gmail.com/password
````
- You can register your own user with any of the above privileges both via the UI of the web-based application, or using
Swagger UI of the REST-based application.

### Database
Both of the two available applications use an in-memory H2 database which is populated during the application startup
with a dummy data. You can easily replace such a database with some kind of full-fledged database like Postgresql. All the SQL scripts
needed to create the initial db user and project schema can be found inside the following folders:

###### web-application
````
timetable-management-system/web-application/src/main/resources/sql
````
###### rest-api-application
````
timetable-management-system/rest-api-application/src/main/resources/sql
````

## Technologies
- Project is built using a Spring technology stack, particularly a Spring Boot framework.
- Spring Data is used to implement repository layer.
- User interface is built using Spring MVC.
- REST API endpoints are built using Spring MVC and Spring HATEOAS.
- Main testing framework is JUnit 5.
- DBUnit is used for testing database-related things.
- JBehave is used for acceptance testing.
- Selenide is used for UI testing.
- Several other libraries are used throughout the project.

Full list of used frameworks/libraries can be found in gradle.build files in each of the project sub-modules.

## Author
With best regards, Vitaliy Dragun!
