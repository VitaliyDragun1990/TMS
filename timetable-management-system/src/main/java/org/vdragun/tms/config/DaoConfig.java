package org.vdragun.tms.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@ComponentScan(basePackages = { "org.vdragun.tms.dao" })
@PropertySource("classpath:db.properties")
@EnableAspectJAutoProxy
public class DaoConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getRequiredProperty("db.url"));
        ds.setPassword(env.getRequiredProperty("db.password"));
        ds.setUsername(env.getRequiredProperty("db.username"));
        ds.setMaximumPoolSize(env.getRequiredProperty("db.maxPoolSize", Integer.class));
        ds.setMinimumIdle(env.getRequiredProperty("db.minIdle", Integer.class));
        ds.setIdleTimeout(env.getRequiredProperty("db.maxIdleTimeout", Integer.class));

        return ds;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DaoExceptionAspect daoExceptionAspect() {
        return new DaoExceptionAspect();
    }
}
