package org.vdragun.tms.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.vdragun.tms.dao.CategoryDao;
import org.vdragun.tms.dao.ClassroomDao;
import org.vdragun.tms.dao.CourseDao;
import org.vdragun.tms.dao.GroupDao;
import org.vdragun.tms.dao.StudentDao;
import org.vdragun.tms.dao.TeacherDao;
import org.vdragun.tms.dao.TimetableDao;
import org.vdragun.tms.util.StartupDataInitializer;

@Configuration
@PropertySource("classpath:generator.properties")
public class StartupDataConfig {

    @Bean
    public StartupDataInitializer startupDataInitializer(
            DataSource dataSource,
            ClassroomDao classroomDao,
            GroupDao groupDao,
            CategoryDao categoryDao,
            TeacherDao teacherDao,
            StudentDao studentDao,
            CourseDao courseDao,
            TimetableDao timetableDao) {
        return new StartupDataInitializer(
                dataSource,
                classroomDao,
                categoryDao,
                groupDao,
                teacherDao,
                studentDao,
                courseDao,
                timetableDao);
    }
}
