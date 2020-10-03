package org.vdragun.tms;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("org.vdragun.tms.core.domain")
@EnableJpaRepositories("org.vdragun.tms.dao.data")
public class SpringBootTestConfiguration {
}
