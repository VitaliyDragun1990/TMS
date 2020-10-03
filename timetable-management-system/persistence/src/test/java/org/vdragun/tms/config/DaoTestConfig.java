package org.vdragun.tms.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@TestConfiguration
@EntityScan("org.vdragun.tms.core.domain")
@EnableJpaRepositories("org.vdragun.tms.dao.data")
@Import(EmbeddedDataSourceConfig.class)
public class DaoTestConfig {

    @Bean
    public DBTestHelper dbTestHelper() {
        return new DBTestHelper();
    }

}
