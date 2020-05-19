package org.vdragun.tms.util.initializer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Initialize application with startup data
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
@PropertySource("classpath:generator.properties")
public class StartupDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StartupDataInitializer.class);

    private DatabaseSchemaCreator databaseSchemaCreator;
    private InitialDataDatabasePopulator initialDataPopulator;
    
    public StartupDataInitializer(
            DatabaseSchemaCreator databaseSchemaCreator,
            InitialDataDatabasePopulator initialDataPopulator) {
        this.databaseSchemaCreator = databaseSchemaCreator;
        this.initialDataPopulator = initialDataPopulator;
    }

    @PostConstruct
    public void initialize() {
        LOG.info("Initializing application with startup data");
        databaseSchemaCreator.createDatabaseSchema();
        initialDataPopulator.populateDatabaseWithInitialData();
    }
}
