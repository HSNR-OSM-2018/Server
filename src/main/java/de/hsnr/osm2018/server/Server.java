package de.hsnr.osm2018.server;

import de.hsnr.osm2018.data.data.FilteredDataProvider;
import de.hsnr.osm2018.provider.provider.PbfProvider;
import de.hsnr.osm2018.server.controller.DataController;
import de.hsnr.osm2018.server.controller.RouteController;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Server {

    private Server() {
        staticFiles.location("/public");
        staticFiles.expireTime(300L);

        before("*", (request, response) -> {
            if (!request.pathInfo().endsWith("/")) {
                response.redirect(request.pathInfo() + "/");
            }
        });
        enableDebugScreen();

        FilteredDataProvider provider = new PbfProvider("ddorf.pbf"); //TODO: adjust when real provider is ready

        get(Config.PATH_DATA, new DataController(provider));
        get(Config.ROUTE_ROUTE, new RouteController(provider));

        System.out.println("Server is running");
    }

    public static void main(String[] args) {
        new Server();
    }

    public static class Config {
        static final String PATH_DATA = "/data/*";
        static final String ROUTE_ROUTE = "/route/*";
    }
}