package org.vdragun.tms.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.vdragun.tms.dao.jpa.TitleConverter;

/**
 * Contains configuration related to DAO layer implementation using Hibernate
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
@ComponentScan(basePackages = { "org.vdragun.tms.dao.jpa" })
@PropertySource({ "classpath:hibernate.properties" })
@EnableAspectJAutoProxy
@EnableTransactionManagement
public class JPADaoConfig {

    @Autowired
    private Environment environment;

    @Bean
    public SessionFactory sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource);
        builder.scanPackages("org.vdragun.tms.core.domain");
        builder.addProperties(hibernateProperties());
        builder.addAttributeConverter(TitleConverter.class, true);

        return builder.buildSessionFactory();
    }

    @Bean
    public DaoExceptionAspect daoExceptionAspect() {
        return new DaoExceptionAspect();
    }

    @Bean
    public PlatformTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.setProperty("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        props.setProperty("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        props.setProperty("hibernate.jdbc.batch_size", environment.getRequiredProperty("hibernate.jdbc.batch_size"));
        props.setProperty("hibernate.hbm2ddl.auto", environment.getRequiredProperty("hibernate.hbm2ddl.auto"));

        return props;
    }
}
