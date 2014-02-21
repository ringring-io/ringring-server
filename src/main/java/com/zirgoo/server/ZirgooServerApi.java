package com.zirgoo.server;

import com.google.inject.Inject;
import com.zirgoo.server.config.ConfigManager;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import com.zirgoo.server.servlet.GuiceConfig;

/**
 * Created by kosztope on 23/01/14.
 */
public class ZirgooServerApi {
    private Server server;
    private final ConfigManager configManager;

    @Inject
    ZirgooServerApi(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public Server run() {
        // Create the server
        server = new Server(configManager.getServerPort());

        // Create a servlet context and add the jersey servlet
        ServletContextHandler sch = new ServletContextHandler(server, "/");

        // Add our Guice listener that includes our bindings
        sch.addEventListener(setUpConfig());

        // Then add GuiceFilter and configure the server to
        // reroute all requests through this filter.
        sch.addFilter(GuiceFilter.class, "/*", null);

        // Must add DefaultServlet for embedded Jetty.
        // Failing to do this will cause 404 errors.
        sch.addServlet(DefaultServlet.class, "/");

        // Start the server
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException("Unable to start server", e);
        }

        return server;
    }

    protected GuiceServletContextListener setUpConfig() {
        return new GuiceConfig();
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public String getUrl() {
        return "http://" + configManager.getServerHost() + ":" + configManager.getServerPort();
    }
}
