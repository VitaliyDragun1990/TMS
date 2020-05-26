package org.vdragun.tms.util.initializer;


import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

/**
 * @author Vitaliy Dragun
 *
 */
@Component
@ConditionalOnProperty(
        name = "tms.stage.development",
        havingValue = "true",
        matchIfMissing = true)
public class DatabaseSchemaCreatorImpl implements DatabaseSchemaCreator {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseSchemaCreatorImpl.class);

    @Value("${generator.initScript}")
    private String initScript;

    private DataSource dataSource;

    public DatabaseSchemaCreatorImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createDatabaseSchema() {
        LOG.info("Creating database schema");
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource(initScript));
        databasePopulator.execute(dataSource);
    }

}
