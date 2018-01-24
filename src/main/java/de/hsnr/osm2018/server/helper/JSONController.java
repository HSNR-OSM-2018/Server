package de.hsnr.osm2018.server.helper;

import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

public abstract class JSONController extends BaseController {

    @Override
    public Object handle(Request request, Response response) throws Exception {
        try {
            return process(request, response);
        } catch (ParameterException e) {
            return error(response, e.getResponse());
        }
    }

    public abstract JSONObject process(Request request, Response response) throws ParameterException;

    private JSONObject response(Response response, int statusCode, String message, Object data) {
        response.type("application/json");
        response.status(statusCode);
        JSONObject res = new JSONObject();
        res.put("message", message);
        res.put("data", data);
        return res;
    }

    protected JSONObject error(Response response, String message) throws JSONException {
        return response(response, HttpStatus.NOT_FOUND_404, message, new JSONObject());
    }

    protected JSONObject success(Response response, String message, JSONObject data) throws JSONException {
        return response(response, HttpStatus.OK_200, message, data);
    }

    protected JSONObject success(Response response, String message, JSONArray data) throws JSONException {
        return response(response, HttpStatus.OK_200, message, data);
    }
}