package org.vdragun.tms.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Contains configuration related to DAO layer implementation using Spring JDBC
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
@ComponentScan(basePackages = { "org.vdragun.tms.dao.jdbc" })
@PropertySource({ "classpath:query.properties" })
@EnableAspectJAutoProxy
public class JdbcDaoConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DaoExceptionAspect daoExceptionAspect() {
        return new DaoExceptionAspect();
    }
}
