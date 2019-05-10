package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DataGetHandler implements HttpHandler {
    private final String url = "jdbc:postgresql://localhost/sample_db";
    private final String user = "ethanzeigler";
    private final String password = "";


    @Override
    public void handle(HttpExchange he) throws IOException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }
        // FIXME real data

        String response = "";
        String query = he.getRequestURI().toString();
        if (query.length() == 0) {
            // empty, provide state
            try {
               Statement stmt = conn.createStatement();
               ResultSet rs = stmt.executeQuery("SELECT * FROM state;");
               StringBuilder sb = new StringBuilder();
               while (rs.next()) {
                   sb.append(rs.getRowId("name")).append("=").append(rs.getRowId("abrev")).append(",");
               }
            } catch (SQLException ex) {
              System.out.println(ex.getMessage());
            }
            // send to html
        } else {
            // get keypairs
            String[] paramStrings = query.split("\\?")[1].split("&");
            Map<String, String> params = new HashMap<String, String>();
            for (String str : paramStrings) {
                params.put(str.split("=")[0], str.split("=")[1]);
            }

            // set response to have "key=value,key=value,key=value..."
            if (params.keySet().contains("category_name")) {
                // ya darn f***ed up
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM statistic_county_link LEFT JOIN county ON statistic_county_link.geofib = county.geofib WHERE county.geofib = " + params.get("county") + ";");
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        //sb.append(rs.getRowId("name")).append("=").append(rs.getRowId("abrev"));
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                //send to html
            } else if (params.keySet().contains("category")) {
                // requesting category names
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT name FROM statistic LEFT JOIN statistic_county_link link ON link.statistic = statistic.id WHERE link.geofib = " + params.get("county") + " AND statistic.category = " + params.get("category") + ";");
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getRowId("category")).append("=").append(rs.getRowId("id")).append(',');
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    response = sb.toString();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                //send to html
            } else if (params.keySet().contains("disaster")) {
                // requesting categories
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT DISTINCT category FROM statistic;");
                    rs.next();
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getRowId("category")).append("=").append(rs.getRowId("category")).append(',');
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    response = sb.toString();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                //send to html
            } else if (params.keySet().contains("county")) {
                // requesting disasters
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT name FROM disaster LEFT JOIN county_disaster_link link ON disaster.disaster_id = link.disaster_id" + " WHERE link.geofib = " + params.get("county") + ";");
                    rs.next();
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getRowId("name")).append("=").append(rs.getRowId("entry_id")).append(',');
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    response = sb.toString();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                //send to html
            } else if (params.keySet().contains("state")) {
                // requesting counties
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT name FROM county WHERE county.state = "+params.get("state") + ";");
                    rs.next();
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getRowId("name")).append("=").append(rs.getRowId("geofib")).append(",");
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    response = sb.toString();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                //send to html
            }
        }
        //System.out.println(query);

        Headers responseHeaders = he.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Content-type", "application/json");
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
