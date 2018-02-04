package de.hsnr.osm2018.server;

import de.hsnr.osm2018.provider.provider.PbfProvider;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TestServer {

    public static void main(String[] args) throws FileNotFoundException {
        /* load the graph into memory */
        System.out.println("Loading graph...");
        long start = System.currentTimeMillis();
        PbfProvider provider = new PbfProvider("GermanyNode.pbf", "GermanyWay.pbf");
        long time = System.currentTimeMillis() - start;
        System.out.println("Graph loaded in " + (time / 1000F) + " seconds");

        new Server(provider).run();

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:4567/"));
            } catch (URISyntaxException | IOException ignored) {}
        }
    }
}