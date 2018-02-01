package de.hsnr.osm2018.server;

import de.hsnr.osm2018.provider.provider.PbfProvider;

import java.io.File;
import java.io.FileNotFoundException;

public class MainServer {

    public static void main(String[] args) {
        PbfProvider provider;
        switch (args.length) {
            case 0:
                System.out.println("At least one parameter is required");
                return;
            case 2:
                try {
                    provider = new PbfProvider(new File(args[0]), new File(args[1]));
                } catch (FileNotFoundException e) {
                    System.out.println("One or both files not found");
                    return;
                }
                break;
            default:
                System.out.println("Only two parameters are allowed");
                return;
        }
        new Server(provider).run();
    }
}