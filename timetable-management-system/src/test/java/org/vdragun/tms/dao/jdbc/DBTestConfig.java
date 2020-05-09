package org.vdragun.tms.dao.jdbc;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Contains spring config for DAO layer integration testing
 * 
 * @author Vitaliy Dragun
 *
 */
@Configuration
public class DBTestConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(EmbeddedDatabaseType.H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("sql/db_schema_seq.sql")
                .build();
    }

    @Bean
    public JdbcTestHelper jdbcTestHelper(DataSource dataSource) {
        return new JdbcTestHelper(dataSource);
    }
}
