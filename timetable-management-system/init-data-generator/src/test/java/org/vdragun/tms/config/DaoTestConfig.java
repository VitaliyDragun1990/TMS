package org.vdragun.tms.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@TestConfiguration
@Import(EmbeddedDataSourceConfig.class)
public class DaoTestConfig {

    @Bean
    public DBTestHelper dbTestHelper() {
        return new DBTestHelper();
    }

}
