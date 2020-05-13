package org.vdragun.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.vdragun.tms.config.JPADaoConfig;
import org.vdragun.tms.config.ServiceConfig;
import org.vdragun.tms.config.StartupDataConfig;
import org.vdragun.tms.config.WebConfig;

/**
 * @author Vitaliy Dragun
 *
 */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class })
@Import({ JPADaoConfig.class, StartupDataConfig.class, ServiceConfig.class, WebConfig.class })
public class TMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(TMSApplication.class, args);
    }
}
