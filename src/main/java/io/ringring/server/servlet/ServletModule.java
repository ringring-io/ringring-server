package io.ringring.server.servlet;

import io.ringring.server.config.setup.guice.ConfigManagerModule;

import io.ringring.server.persistence.repositories.setup.guice.UserRepositoryModule;
import io.ringring.server.persistence.setup.guice.PersistenceModule;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kosztope on 23/01/14.
 */

public class ServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        installConfigModule();
        installPersistenceModule();
        installUserRepositoryModule();

        // Set init params for Jersey
        Map<String, String> params = new HashMap<String, String>();
        params.put("com.sun.jersey.config.property.packages", "io.ringring.server.resources");
        params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        // Route all requests through GuiceContainer
        serve("/*").with(GuiceContainer.class, params);
    }

    protected void installConfigModule() {
        install(new ConfigManagerModule());
    }

    protected void installPersistenceModule() {
        install(new PersistenceModule());
    }

    protected void installUserRepositoryModule() {
        install(new UserRepositoryModule());
    }
}