package de.hsnr.osm2018.server.controller;

import de.hsnr.osm2018.data.provider.FilteredDataProvider;
import de.hsnr.osm2018.server.helper.JSONController;
import de.hsnr.osm2018.server.helper.ParameterException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

public class DataController extends JSONController {

    private FilteredDataProvider mProvider;

    public DataController(FilteredDataProvider provider) {
        this.mProvider = provider;
    }

    @Override
    public JSONObject process(Request request, Response response) throws ParameterException {
        String[] path = request.pathInfo().substring(1).split("/");
        switch (path[1]) {
            case "point":
                return handlePoint(request, response);
            default:
                return error(response, "route not found");
        }
    }

    private JSONObject handlePoint(Request request, Response response) throws ParameterException {
        double latitude = getCoordinateValue(request, "latitude"), longitude = getCoordinateValue(request, "longitude");
        double vicinityRadius = 0.01D; //TODO: adjust to a more realistic value
        if (hasParameter(request, "radius")) {
            vicinityRadius = getRadiusValue(request, "radius", 1D);
        }
        return success(response, "graph around the point", mProvider.getGraph(latitude, longitude, vicinityRadius).toJSON());
    }
}