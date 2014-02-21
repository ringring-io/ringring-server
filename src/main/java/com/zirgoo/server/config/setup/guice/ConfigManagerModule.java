package com.zirgoo.server.config.setup.guice;

import com.google.inject.AbstractModule;
import com.zirgoo.server.config.ConfigManager;
import com.zirgoo.server.config.setup.PropertiesConfigManagerImpl;

/**
 * Created by kosztope on 24/01/14.
 */
public class ConfigManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigManager.class).to(PropertiesConfigManagerImpl.class);
    }
}
