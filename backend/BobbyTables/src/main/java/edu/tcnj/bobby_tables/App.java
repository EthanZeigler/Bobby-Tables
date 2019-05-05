package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException {
        int port = 9000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/fields", new FieldsGetHandler());
        server.setExecutor(null);
        server.start();
    }
}
