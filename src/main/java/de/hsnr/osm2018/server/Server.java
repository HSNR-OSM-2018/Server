package de.hsnr.osm2018.server;

import de.hsnr.osm2018.data.provider.FilteredDataProvider;
import de.hsnr.osm2018.server.controller.DataController;
import de.hsnr.osm2018.server.controller.RouteController;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Server {

    private FilteredDataProvider mProvider;

    public Server(FilteredDataProvider provider) {
        this.mProvider = provider;
    }

    public void run() {
        /* configure static content */
        staticFiles.location("/public");
        staticFiles.expireTime(300L);
        before("*", (request, response) -> {
            if (!request.pathInfo().endsWith("/")) {
                response.redirect(request.pathInfo() + "/");
            }
        });
        enableDebugScreen();

        get(Config.PATH_DATA, new DataController(mProvider));
        get(Config.ROUTE_ROUTE, new RouteController(mProvider));

        System.out.println("Server is running");
    }

    public static class Config {
        static final String PATH_DATA = "/data/*";
        static final String ROUTE_ROUTE = "/route/*";
    }
}