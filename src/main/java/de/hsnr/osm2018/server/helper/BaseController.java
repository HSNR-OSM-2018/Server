package de.hsnr.osm2018.server.helper;

import spark.Request;
import spark.Route;

public abstract class BaseController implements Route {

    protected boolean hasParameter(Request request, String key) {
        return request.queryParams().contains(key);
    }

    protected String getParameter(Request request, String key) throws ParameterException {
        if (!hasParameter(request, key)) {
            throw new ParameterException("parameter with key `" + key + "` not provided");
        }
        return request.queryParams(key);
    }

    protected String getParameter(Request request, String key, String defaultValue) {
        try {
            return getParameter(request, key);
        } catch (ParameterException e) {
            return defaultValue;
        }
    }

    protected double getDoubleParameter(Request request, String key) throws ParameterException {
        try {
            return Double.parseDouble(getParameter(request, key));
        } catch (NumberFormatException e) {
            throw new ParameterException("parameter with key `" + key + "` is not a valid floating number");
        }
    }

    protected double getDoubleParameter(Request request, String key, double defaultValue) throws ParameterException {
        try {
            return Double.parseDouble(getParameter(request, key));
        } catch (ParameterException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            throw new ParameterException("parameter with key `" + key + "` is not a valid floating number");
        }
    }

    protected long getLongParameter(Request request, String key) throws ParameterException {
        try {
            return Long.parseLong(getParameter(request, key));
        } catch (NumberFormatException e) {
            throw new ParameterException("parameter with key `" + key + "` is not a valid floating number");
        }
    }

    protected long getLongParameter(Request request, String key, long defaultValue) throws ParameterException {
        try {
            return Long.parseLong(getParameter(request, key));
        } catch (ParameterException e) {
            return defaultValue;
        } catch (NumberFormatException e) {
            throw new ParameterException("parameter with key `" + key + "` is not a valid floating number");
        }
    }

    protected double getCoordinateValue(Request request, String key) throws ParameterException {
        double value = getDoubleParameter(request, key);
        if (value > 180D || value < -180D) {
            throw new ParameterException("parameter with key `" + key + "` is out of bound");
        }
        return value;
    }

    protected double getRadiusValue(Request request, String key, double maxValue) throws ParameterException {
        double value = getDoubleParameter(request, key);
        if (value < 0D || value > maxValue) {
            throw new ParameterException("parameter with key `" + key + "` is out of bound");
        }
        return value;
    }
}