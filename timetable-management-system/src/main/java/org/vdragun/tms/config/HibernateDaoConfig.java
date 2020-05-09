package org.vdragun.tms.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

/**
 * Contains configuration related to DAO layer implementation using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
@ComponentScan(basePackages = { "org.vdragun.tms.dao.hibernate" })
@PropertySource({ "classpath:hibernate.properties" })
@EnableAspectJAutoProxy
public class HibernateDaoConfig {

    @Autowired
    private Environment environment;

    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("org.vdragun.tms.core.domain");
        sessionFactoryBean.setHibernateProperties(hibernateProperties());

        return sessionFactoryBean;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        props.setProperty("hibernate.jdbc.batch_size", environment.getRequiredProperty("hibernate.jdbc.batch_size"));
        props.setProperty("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));

        return props;
    }

    @Bean
    public DaoExceptionAspect daoExceptionAspect() {
        return new DaoExceptionAspect();
    }

}
