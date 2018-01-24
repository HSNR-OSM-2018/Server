package de.hsnr.osm2018.server;

import de.hsnr.osm2018.data.provider.FilteredDataProvider;
import de.hsnr.osm2018.provider.provider.PbfProvider;
import de.hsnr.osm2018.server.controller.DataController;
import de.hsnr.osm2018.server.controller.RouteController;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Server {

    private Server() throws IOException {
        /* load the graph into memory */
        System.out.println("Loading graph...");
        long start = System.currentTimeMillis();
        FilteredDataProvider provider = new PbfProvider("newDdorfFilter.pbf"); //TODO: adjust when real provider is ready
        long time = System.currentTimeMillis() - start;
        System.out.println("Graph loaded in " + (time / 1000) + " seconds");

        /* configure static content */
        staticFiles.location("/public");
        staticFiles.expireTime(300L);
        before("*", (request, response) -> {
            if (!request.pathInfo().endsWith("/")) {
                response.redirect(request.pathInfo() + "/");
            }
        });
        enableDebugScreen();

        get(Config.PATH_DATA, new DataController(provider));
        get(Config.ROUTE_ROUTE, new RouteController(provider));

        System.out.println("Server is running");
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:4567/"));
            } catch (URISyntaxException ignored) {}
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    public static class Config {
        static final String PATH_DATA = "/data/*";
        static final String ROUTE_ROUTE = "/route/*";
    }
}