package io.ringring.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.ringring.server.config.setup.guice.ConfigManagerModule;
import io.ringring.server.db.baseline.CreateBaseline;

/**
 * Created by kosztope on 23/01/14.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Injector configManagerInjector = Guice.createInjector(new ConfigManagerModule());

        CreateBaseline createBaseline = configManagerInjector.getInstance(CreateBaseline.class);
        createBaseline.create();

        ringringServerApi ringringServerApi = configManagerInjector.getInstance(ringringServerApi.class);
        ringringServerApi.run().join();
    }
}
