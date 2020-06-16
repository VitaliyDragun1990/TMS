package org.vdragun.tms.util.initializer;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class StartupDataInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(StartupDataInitializer.class);

    private InitialDataDatabasePopulator initialDataPopulator;
    
    public StartupDataInitializer(
            InitialDataDatabasePopulator initialDataPopulator) {
        this.initialDataPopulator = initialDataPopulator;
    }

    @PostConstruct
    public void initialize() {
        LOG.info("Initializing application with startup data");
        initialDataPopulator.populateDatabaseWithInitialData();
    }
}
