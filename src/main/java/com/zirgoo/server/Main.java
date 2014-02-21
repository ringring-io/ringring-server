package com.zirgoo.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.zirgoo.server.config.setup.guice.ConfigManagerModule;
import com.zirgoo.server.db.baseline.CreateBaseline;

/**
 * Created by kosztope on 23/01/14.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        Injector configManagerInjector = Guice.createInjector(new ConfigManagerModule());

        CreateBaseline createBaseline = configManagerInjector.getInstance(CreateBaseline.class);
        createBaseline.create();

        ZirgooServerApi zirgooServerApi = configManagerInjector.getInstance(ZirgooServerApi.class);
        zirgooServerApi.run().join();
    }
}
