package org.vdragun.tms.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiTemplate;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Contains database-related configuration
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
@PropertySource("classpath:db.properties")
public class DBConfig {

    @Autowired
    private Environment env;

//    @Bean
    public DataSource dataSource() {
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

    @Bean
    public DataSource jndiDataSource() throws NamingException {
        return (DataSource) new JndiTemplate().lookup(env.getRequiredProperty("db.jndi.url"));
    }

}
