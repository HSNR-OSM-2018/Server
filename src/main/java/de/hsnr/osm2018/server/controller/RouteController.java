package de.hsnr.osm2018.server.controller;

import de.hsnr.osm2018.core.algoritms.AStar;
import de.hsnr.osm2018.data.data.FilteredDataProvider;
import de.hsnr.osm2018.data.graph.Node;
import de.hsnr.osm2018.data.graph.NodeContainer;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

public class RouteController extends JSONController {

    private FilteredDataProvider mProvider;

    public RouteController(FilteredDataProvider provider) {
        this.mProvider = provider;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String[] path = request.pathInfo().substring(1).split("/");
        switch (path[1]) {
            case "node":
                return handleNode(request, response, path);
            case "point":
                return handlePoint(request, response, path);
            default:
                return error(response, "route not found");
        }
    }

    private JSONObject handleNode(Request request, Response response, String[] path) {
        if (path.length < 5) {
            return error(response, "invalid route. usage: '/data/point/:<shortest|fastest>/:start/:destination/'");
        }
        boolean shortest;
        if (path[2].equalsIgnoreCase("shortest")) {
            shortest = true;
        } else if (path[2].equalsIgnoreCase("fastest")) {
            shortest = false;
        } else {
            return error(response, "Invalid value for type. allowed values are: shortest, fastest");
        }
        Node start, destination;
        try {
            start = getNode(path[3]);
        } catch (Exception e) {
            return error(response, "start node not found");
        }
        try {
            destination = getNode(path[4]);
        } catch (Exception e) {
            return error(response, "destination node not found");
        }
        AStar algorithm = new AStar();
        if (shortest) {
            algorithm.runAStar(mProvider.getGraph(), start, destination);
        } else {
            algorithm.runAStarWithSpeed(mProvider.getGraph(), start, destination);
        }
        JSONArray data = new JSONArray();
        int id = 0;
        for (NodeContainer n : algorithm.getPath(start, destination)) {
            id++;
            JSONObject element = new JSONObject();
            element.put("id", id);
            element.put("lat", n.getLatitude());
            element.put("lon", n.getLongitude());
            element.put("node", n.getId());
            element.put("w", 0);
            data.put(element);
        }
        return success(response, "path data", data);
    }

    private JSONObject handlePoint(Request request, Response response, String[] path) {
        if (path.length < 7) {
            return error(response, "invalid route. usage: '/data/point/:<shortest|fastest>/:startLatitude/:startLongitude/:destinationLatitude/:destinationLongitude/'");
        }
        boolean shortest;
        if (path[2].equalsIgnoreCase("shortest")) {
            shortest = true;
        } else if (path[2].equalsIgnoreCase("fastest")) {
            shortest = false;
        } else {
            return error(response, "Invalid value for type. allowed values are: shortest, fastest");
        }
        double startLatitude, startLongitude, destinationLatitude, destinationLongitude;
        try {
            startLatitude = getCoordinateValue(path[3]);
            startLongitude = getCoordinateValue(path[4]);
            destinationLatitude = getCoordinateValue(path[5]);
            destinationLongitude = getCoordinateValue(path[6]);
        } catch (NumberFormatException e) {
            return error(response, "invalid value");
        }
        Node start = mProvider.getGraph().getNearest(startLatitude, startLongitude), destination = mProvider.getGraph().getNearest(destinationLatitude, destinationLongitude);
        System.out.println("selected nodes: " + start + " - " + destination);
        AStar algorithm = new AStar();
        if (shortest) {
            algorithm.runAStar(mProvider.getGraph(), start, destination);
        } else {
            algorithm.runAStarWithSpeed(mProvider.getGraph(), start, destination);
        }
        JSONArray data = new JSONArray();
        int id = 0;
        for (NodeContainer n : algorithm.getPath(start, destination)) {
            id++;
            JSONObject element = new JSONObject();
            element.put("id", id);
            element.put("lat", n.getLatitude());
            element.put("lon", n.getLongitude());
            element.put("node", n.getId());
            element.put("w", 0);
            data.put(element);
        }
        return success(response, "path data", data);
    }

    private Node getNode(String requestValue) {
        Node node = mProvider.getGraph().getNode(Long.valueOf(requestValue));
        if (node == null) {
            throw new NumberFormatException("invalid node id");
        }
        return node;
    }

    private double getCoordinateValue(String requestValue) throws NumberFormatException {
        double value = Double.parseDouble(requestValue);
        if (value > 180D || value < -180D) {
            throw new NumberFormatException("value out of bound");
        }
        return value;
    }
}
