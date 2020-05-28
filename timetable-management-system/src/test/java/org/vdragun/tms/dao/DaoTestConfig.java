package org.vdragun.tms.dao;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.vdragun.tms.EmbeddedDataSourceConfig;

@TestConfiguration
@Import(EmbeddedDataSourceConfig.class)
public class DaoTestConfig {

    @Bean
    public DBTestHelper dbTestHelper() {
        return new DBTestHelper();
    }

}
