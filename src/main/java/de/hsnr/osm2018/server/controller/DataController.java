package de.hsnr.osm2018.server.controller;

import de.hsnr.osm2018.data.data.FilteredDataProvider;
import spark.Request;
import spark.Response;

public class DataController extends JSONController {

    private FilteredDataProvider mProvider;

    public DataController(FilteredDataProvider provider) {
        this.mProvider = provider;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String[] path = request.pathInfo().substring(1).split("/");
        switch (path[1]) {
            case "point":
                if (path.length < 4) {
                    return error(response, "invalid route. usage: '/data/point/:latitude/:longitude/'");
                }
                double latitude;
                try {
                    latitude = Double.parseDouble(path[2]);
                    if (latitude > 180D || latitude < -180D) {
                        throw new NumberFormatException("value out of bound");
                    }
                } catch (NumberFormatException e) {
                    return error(response, "invalid value for latitude");
                }
                double longitude;
                try {
                    longitude = Double.parseDouble(path[3]);
                    if (longitude > 180D || longitude < -180D) {
                        throw new NumberFormatException("value out of bound");
                    }
                } catch (NumberFormatException e) {
                    return error(response, "invalid value for longitude");
                }
                double vicinityRadius = 0.01D; //TODO: adjust to a more realistic value
                if (request.queryParams().contains("radius")) {
                    try {
                        vicinityRadius = Double.parseDouble(request.queryParams("radius"));
                        if (vicinityRadius < 0D || vicinityRadius > 1D) {
                            throw new NumberFormatException("value out of bound");
                        }
                    } catch (NumberFormatException e) {
                        return error(response, "invalid value for radius");
                    }
                }
                return success(response, "graph around the point", mProvider.getGraph(latitude, longitude, vicinityRadius).toJSON());
            default:
                return error(response, "route not found");
        }
    }
}