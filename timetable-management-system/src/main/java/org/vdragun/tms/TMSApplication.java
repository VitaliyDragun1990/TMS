package org.vdragun.tms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @author Vitaliy Dragun
 *
 */
@SpringBootApplication(
        scanBasePackages = "org.vdragun.tms.config",
        exclude = HibernateJpaAutoConfiguration.class)
public class TMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(TMSApplication.class, args);
    }
}
