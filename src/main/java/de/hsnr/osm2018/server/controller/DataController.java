package de.hsnr.osm2018.server.controller;

import de.hsnr.osm2018.data.provider.FilteredDataProvider;
import org.json.JSONObject;
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
                return handlePoint(request, response, path);
            default:
                return error(response, "route not found");
        }
    }

    private JSONObject handlePoint(Request request, Response response, String[] path) {
        if (path.length < 4) {
            return error(response, "invalid route. usage: '/data/point/:latitude/:longitude/'");
        }
        double latitude;
        try {
            latitude = getCoordinateValue(path[2]);
        } catch (NumberFormatException e) {
            return error(response, "invalid value for latitude");
        }
        double longitude;
        try {
            longitude = getCoordinateValue(path[3]);
        } catch (NumberFormatException e) {
            return error(response, "invalid value for longitude");
        }
        double vicinityRadius = 0.01D; //TODO: adjust to a more realistic value
        if (request.queryParams().contains("radius")) {
            try {
                vicinityRadius = getRadiusValue(request.queryParams("radius"), 1D);
            } catch (NumberFormatException e) {
                return error(response, "invalid value for radius");
            }
        }
        return success(response, "graph around the point", mProvider.getGraph(latitude, longitude, vicinityRadius).toJSON());
    }

    private double getCoordinateValue(String requestValue) throws NumberFormatException {
        double value = Double.parseDouble(requestValue);
        if (value > 180D || value < -180D) {
            throw new NumberFormatException("value out of bound");
        }
        return value;
    }

    private double getRadiusValue(String requestValue, double maxValue) throws NumberFormatException {
        double value = Double.parseDouble(requestValue);
        if (value < 0D || value > maxValue) {
            throw new NumberFormatException("value out of bound");
        }
        return value;
    }
}