package com.zirgoo.server.persistence.setup.guice;

import com.google.inject.AbstractModule;
import com.zirgoo.server.config.setup.guice.ConfigManagerModule;
import com.zirgoo.server.persistence.ConnectionManager;
import com.zirgoo.server.persistence.setup.JDBCPooledConnectionManager;

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
