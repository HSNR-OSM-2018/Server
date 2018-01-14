package de.hsnr.osm2018.server;

import de.hsnr.osm2018.data.data.FilteredDataProvider;
import de.hsnr.osm2018.provider.RandomProvider;
import de.hsnr.osm2018.server.controller.DataController;

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

        FilteredDataProvider provider = new RandomProvider(500000); //TODO: adjust when real provider is ready

        DataController dataController = new DataController(provider);

        get(Config.PATH_DATA, dataController);

        System.out.println("Server is running");
    }

    public static void main(String[] args) {
        new Server();
    }

    public static class Config {
        public static final String PATH_DATA = "/data/*";
        public static final String ROUTE_ROUTE = "/route/*";
    }
}