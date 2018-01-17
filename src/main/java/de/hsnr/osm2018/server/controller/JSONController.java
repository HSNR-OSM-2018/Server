package de.hsnr.osm2018.server.controller;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Response;
import spark.Route;

public abstract class JSONController implements Route {

    protected JSONObject error(Response response, String message) throws JSONException {
        response.type("application/json");
        response.status(HttpStatus.NOT_FOUND_404);
        JSONObject res = new JSONObject();
        res.put("message", message);
        res.put("data", new JSONObject());
        return res;
    }

    protected JSONObject success(Response response, String message, JSONObject data) throws JSONException {
        response.type("application/json");
        response.status(HttpStatus.OK_200);
        JSONObject res = new JSONObject();
        res.put("message", message);
        res.put("data", data);
        return res;
    }

    protected JSONObject success(Response response, String message, JSONArray data) throws JSONException {
        response.type("application/json");
        response.status(HttpStatus.OK_200);
        JSONObject res = new JSONObject();
        res.put("message", message);
        res.put("data", data);
        return res;
    }
}