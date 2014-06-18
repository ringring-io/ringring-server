package io.ringring.server.persistence.setup.guice;

import com.google.inject.AbstractModule;
import io.ringring.server.config.setup.guice.ConfigManagerModule;
import io.ringring.server.persistence.ConnectionManager;
import io.ringring.server.persistence.setup.JDBCPooledConnectionManager;

/**
 * Created by kosztope on 23/01/14.
 */
public class PersistenceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConnectionManager.class).to(JDBCPooledConnectionManager.class);
        install(new ConfigManagerModule());
    }
}
