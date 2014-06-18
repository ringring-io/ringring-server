package io.ringring.server;

import com.google.inject.Inject;
import io.ringring.server.config.ConfigManager;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
import io.ringring.server.servlet.GuiceConfig;

/**
 * Created by kosztope on 23/01/14.
 */
public class ringringServerApi {
    private Server server;
    private final ConfigManager configManager;

    @Inject
    ringringServerApi(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public Server run() {
        // Create the server
        server = new Server();

        // Create non-ssl connector
        SocketConnector connector = new SocketConnector();
        connector.setHost(configManager.getServerHost());
        connector.setPort(configManager.getServerPort());
        Connector[] connectors = new Connector[] {connector};

        // Create ssl connector
        if (configManager.getSslEnabled()) {
            SslContextFactory sslContextFactory = new SslContextFactory(configManager.getSslKeystorePath());
            sslContextFactory.setKeyStorePassword(configManager.getSslKeystorePassword());
            sslContextFactory.setKeyManagerPassword(configManager.getSslKeymanagerPassword());

            SslSocketConnector sslConnector = new SslSocketConnector(sslContextFactory);
            sslConnector.setHost(configManager.getServerHost());
            sslConnector.setPort(configManager.getSslPort());

            connectors = new Connector[] {connector, sslConnector};
        }

        // Add connectors to server instance
        server.setConnectors(connectors);

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
