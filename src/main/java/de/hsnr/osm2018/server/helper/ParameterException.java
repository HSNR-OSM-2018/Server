package de.hsnr.osm2018.server.helper;

public class ParameterException extends Exception {

    private String response;

    public ParameterException(String response) {
        super(response);
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}