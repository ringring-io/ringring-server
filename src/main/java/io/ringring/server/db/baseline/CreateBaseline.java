package io.ringring.server.db.baseline;

import com.google.inject.Inject;
import com.googlecode.flyway.core.Flyway;
import io.ringring.server.config.ConfigManager;

/**
 * Created by kosztope on 27/01/14.
 */
public class CreateBaseline {
    private final ConfigManager configManager;

    @Inject
    CreateBaseline(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void create() {

        // Create the Flyway instance
        Flyway flyway = new Flyway();

        // Point it to the database
        flyway.setDataSource(configManager.getDatabaseUrl(), configManager.getDatabaseUser(), configManager.getDatabasePassword());

        // Start the migration
        //flyway.setInitOnMigrate(true);
        flyway.migrate();
    }
}
