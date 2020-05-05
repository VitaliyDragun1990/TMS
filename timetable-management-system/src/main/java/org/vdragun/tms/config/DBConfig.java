package org.vdragun.tms.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:db.properties")
public class DBConfig {

    @Bean
    public DataSource dataSource(Environment env) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(env.getRequiredProperty("db.url"));
        dataSource.setDriverClassName(env.getRequiredProperty("db.driverClassName"));
        dataSource.setPassword(env.getRequiredProperty("db.password"));
        dataSource.setUsername(env.getRequiredProperty("db.username"));
        dataSource.setMaximumPoolSize(env.getRequiredProperty("db.maxPoolSize", Integer.class));
        dataSource.setMinimumIdle(env.getRequiredProperty("db.minIdle", Integer.class));
        dataSource.setIdleTimeout(env.getRequiredProperty("db.maxIdleTimeout", Integer.class));

        return dataSource;
    }

}
