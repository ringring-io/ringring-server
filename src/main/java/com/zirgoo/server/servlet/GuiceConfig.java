package com.zirgoo.server.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Created by kosztope on 23/01/14.
 */

public class GuiceConfig extends GuiceServletContextListener {

    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector();

        return Guice.createInjector(new ServletModule());
    }
}