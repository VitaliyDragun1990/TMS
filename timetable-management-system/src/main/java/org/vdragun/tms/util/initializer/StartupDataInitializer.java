package org.vdragun.tms.util.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Initialize application with startup data
 * 
 * @author Vitaliy Dragun
 *
 */
@Component
@ConditionalOnProperty(
        name = "startup.data.initialize",
        havingValue = "true",
        matchIfMissing = true)
public class StartupDataInitializer implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(StartupDataInitializer.class);

    private InitialDataDatabasePopulator initialDataPopulator;
    
    public StartupDataInitializer(
            InitialDataDatabasePopulator initialDataPopulator) {
        this.initialDataPopulator = initialDataPopulator;
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("Initializing application with startup data");
        initialDataPopulator.populateDatabaseWithInitialData();
    }
}
