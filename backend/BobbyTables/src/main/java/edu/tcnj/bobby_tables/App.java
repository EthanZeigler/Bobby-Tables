package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sun.net.httpserver.HttpServer;
/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) throws IOException, SQLException {
        int port = 9001;
        DriverManager.registerDriver(new org.postgresql.Driver());
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("server started at " + port);
        server.createContext("/fields", new DataGetHandler());
        server.setExecutor(null);
        server.start();
    }
}
