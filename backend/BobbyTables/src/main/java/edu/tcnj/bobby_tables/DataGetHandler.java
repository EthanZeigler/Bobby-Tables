package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DataGetHandler implements HttpHandler {
    Connection c = null;
    Statement stmt = null;
    Class.forName("org.postgresql.Driver");
    c = DriveManager.getConnection("\\connection to database location","\\username","\\password");
    c.setAutoCommit(false);
    @Override
    public void handle(HttpExchange he) throws IOException {
        // FIXME real data

        String response = "";
        String query = he.getRequestURI().toString();
        stmt = c.createStatement();
        if (query.length() == 0) {
            // empty, provide state
            ResultSet rs = stmt.executeQuery("SELECT name FROM state");
            // send to html
        } else {
            // get keypairs
            String[] paramStrings = query.split("?")[1].split("&");
            Map<String, String> params = new HashMap<String, String>();
            for (String str : paramStrings) {
                params.put(str.split("=")[0], str.split("=")[1]);
            }

            // set response to have "key=value,key=value,key=value..."
            if (params.keySet().contains("category_name")) {
                // ya darn f***ed up

		ResultSet data = 

		//ResultSet disasters = stmt.executeQuery("SELECT * FROM statistic_county_link LEFT JOIN county ON statistic_county_link.geofib = county.geofib WHERE county.geofib = " + params.get("county") + ";");
                //send to html
            } else if (params.keySet().contains("category")) {
                // requesting category names
                ResultSet rs = stmt.executeQuery("SELECT name FROM statistic WHERE catagory = "+params.get("catagory") + ";");
                //send to html
            } else if (params.keySet().contains("disaster")) {
                // requesting categories
                ResultSet rs = stmt.executeQuery("SELECT catagory FROM statistic;");
                //send to html
            } else if (params.keySet().contains("county")) {
                // requesting disasters
                ResultSet rs = stmt.executeQuery("SELECT name FROM disaster JOIN county_disaster_link ON disaster_id = entry_id WHERE geofib = "+params.get("county") + ";");
                //send to html
            } else if (params.keySet().contains("state")) {
                // requesting counties
                ResultSet rs = stmt.executeQuery("SELECT name FROM county WHERE state = "+params.get("state") + ";");
                //send to html
            }
        }
        stmt.close();
        //System.out.println(query);

        Headers responseHeaders = he.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Content-type", "application/json");
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    c.close();
}
