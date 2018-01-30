package de.hsnr.osm2018.server.controller;

import de.hsnr.osm2018.core.algoritms.DijkstraAStar;
import de.hsnr.osm2018.core.algoritms.DistanceAStar;
import de.hsnr.osm2018.core.algoritms.SpeedAStar;
import de.hsnr.osm2018.data.graph.Graph;
import de.hsnr.osm2018.data.path.PathContainer;
import de.hsnr.osm2018.data.path.PathFinder;
import de.hsnr.osm2018.data.path.PathGraphContainer;
import de.hsnr.osm2018.data.provider.FilteredDataProvider;
import de.hsnr.osm2018.data.graph.Node;
import de.hsnr.osm2018.server.helper.JSONController;
import de.hsnr.osm2018.server.helper.ParameterException;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.List;
import java.util.logging.Logger;

public class RouteController extends JSONController {

    private Logger logger = Logger.getLogger(RouteController.class.getSimpleName());

    private FilteredDataProvider mProvider;

    public RouteController(FilteredDataProvider provider) {
        this.mProvider = provider;
    }

    @Override
    public JSONObject process(Request request, Response response) throws ParameterException {
        Graph graph = mProvider.getGraph();
        Node start = null, destination = null;
        double startLatitude, startLongitude, destinationLatitude, destinationLongitude;
        if (hasParameter(request, "startNode")) {
            start = getNode(graph, request, "startNode");
            startLatitude = start.getLatitude();
            startLongitude = start.getLongitude();
        } else if (hasParameter(request, "startLatitude") && hasParameter(request, "startLongitude")) {
            startLatitude = getDoubleParameter(request, "startLatitude");
            startLongitude = getDoubleParameter(request, "startLongitude");
        } else {
            return error(response, "no start provided");
        }
        if (hasParameter(request, "destinationNode")) {
            destination = getNode(graph, request, "destinationNode");
            destinationLatitude = destination.getLatitude();
            destinationLongitude = destination.getLongitude();
        } else if (hasParameter(request, "destinationLatitude") && hasParameter(request, "destinationLongitude")) {
            destinationLatitude = getDoubleParameter(request, "destinationLatitude");
            destinationLongitude = getDoubleParameter(request, "destinationLongitude");
        } else {
            return error(response, "no destination");
        }
        PathGraphContainer container = mProvider.getPathGraphContainer(startLatitude, startLongitude, destinationLatitude, destinationLongitude);
        graph = container.getGraph();
        if (start == null) {
            start = container.getStart();
        }
        if (destination == null) {
            destination = container.getDestination();
        }
        PathFinder algorithm;
        switch (getParameter(request, "algorithm").toLowerCase()) {
            case "shortest":
                algorithm = new DistanceAStar(graph);
                break;
            case "fastest":
                algorithm = new SpeedAStar(graph);
                break;
            case "dijkstra":
                algorithm = new DijkstraAStar(graph);
                break;
            default:
                return error(response, "algorithm not supported");
        }
        logger.info("navigating with algorithm " + algorithm.getClass().getName() + " from node #" + start.getId() + " (" + destination.getEdges().size() + " edges) to node #" + destination.getId() + " (" + destination.getEdges().size() + " edges)");
        boolean success = algorithm.run(start, destination);
        if (!success) {
            return error(response, "path not found");
        }
        return success(response, "path data", buildPath(algorithm.getPath()));
    }

    private Node getNode(Graph graph, Request request, String key) throws ParameterException {
        Node node = graph.getNode(getLongParameter(request, key));
        if (node == null) {
            throw new ParameterException("node for parameter `" + key + "` not found");
        }
        return node;
    }

    private Node getNode(Graph graph, double latitude, double longitude) throws ParameterException {
        Node node = graph.getNearest(latitude, longitude);
        if (node == null) {
            throw new ParameterException("node for latitude `" + latitude + "` and longitude `" + longitude + "` not found");
        }
        return node;
    }

    private JSONArray buildPath(List<PathContainer> path) {
        JSONArray data = new JSONArray();
        int id = 0;
        for (PathContainer n : path) {
            id++;
            JSONObject element = new JSONObject();
            element.put("id", id);
            element.put("lat", n.getNode().getLatitude());
            element.put("lon", n.getNode().getLongitude());
            element.put("node", n.getNode().getId());
            element.put("w", n.getDistance());
            data.put(element);
        }
        return data;
    }
}