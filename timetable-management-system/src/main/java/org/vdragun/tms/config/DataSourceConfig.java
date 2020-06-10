package org.vdragun.tms.config;

import java.io.IOException;
import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.jndi.JndiObjectFactoryBean;


@Configuration
public class DataSourceConfig {

    @Bean
    public Properties dataSourceProperties() throws IOException {
        return PropertiesLoaderUtils.loadProperties(new ClassPathResource("/db.properties"));
    }

    @ConditionalOnProperty(
            name = "jndi.datasource",
            havingValue = "true",
            matchIfMissing = true)
    @Bean(destroyMethod = "")
    public DataSource dataSource(Properties dataSourceProperties) throws NamingException {
        JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
        factoryBean.setJndiName("java:comp/env/" + dataSourceProperties.getProperty("name"));
        factoryBean.setProxyInterface(DataSource.class);
        factoryBean.setLookupOnStartup(false);
        factoryBean.afterPropertiesSet();

        return (DataSource) factoryBean.getObject();
    }

}
