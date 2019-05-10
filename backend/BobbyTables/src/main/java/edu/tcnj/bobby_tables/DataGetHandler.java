package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.postgresql.Driver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DataGetHandler implements HttpHandler {
    private final String url = "jdbc:postgresql://localhost:5432/sample_db";
    private final String user = "ethanzeigler";
    private final String password = "";


    @Override
    public void handle(HttpExchange he) {
        try {
            Connection conn = null;
            System.out.println("Incomming connection" + he.getRequestURI().toString());
            try {
                DriverManager.registerDriver(new org.postgresql.Driver());
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                return;
            }
            // FIXME real data

            String response = "";
            String query = he.getRequestURI().toString();
            if (!query.contains("?")) {
                // empty, provide state
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM state;");
                    StringBuilder sb = new StringBuilder();
                    while (rs.next()) {
                        sb.append(rs.getString(2)).append("=").append(rs.getString(1)).append(",");
                    }
                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    response = sb.toString();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
                // send to html
            } else {
                // get keypairs
                String[] paramStrings = query.split("\\?")[1].split("&");
                Map<String, String> params = new HashMap<String, String>();
                for (String str : paramStrings) {
                    try {
                        params.put(str.split("=")[0], str.split("=")[1]);
                    } catch(Exception e) {

                    }
                }

                // set response to have "key=value,key=value,key=value..."
                if (params.keySet().contains("category_name")) {
                    // ya darn f***ed up
                    try {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT statistic.data FROM statistic_county_link LEFT JOIN statistic ON statistic_county_link.statistic = statistic.id WHERE statistic_county_link.geofib = '" + params.get("county") + "';");
                        StringBuilder sb = new StringBuilder("[");
                        while (rs.next()) {
                            sb.append(rs.getInt(1)).append(",");
                        }
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }
                        sb.append("]");
                        response = sb.toString();
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                    //send to html
                } else if (params.keySet().contains("category")) {
                    // requesting category names
                    try {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT DISTINCT name FROM statistic LEFT JOIN statistic_county_link link ON link.statistic = statistic.id WHERE link.geofib = '" + params.get("county") + "' AND statistic.category = '" + params.get("category") + "';");
                        ResultSet disaster = stmt.executeQuery("SELECT disaster.date from disaster where entry_id = " + params.get("disaster") + ";");
                        Date date = disaster.getDate(1);
                        date.toString();
                        StringBuilder sb = new StringBuilder();
                        while (rs.next()) {
                            sb.append(rs.getString(1)).append("=").append(rs.getString(1)).append(',');
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
                        StringBuilder sb = new StringBuilder();
                        while (rs.next()) {
                            sb.append(rs.getString(1)).append("=").append(rs.getString(1)).append(',');
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
                        ResultSet rs = stmt.executeQuery("SELECT disaster.name, disaster.entry_id FROM disaster LEFT JOIN county_disaster_link link ON disaster.entry_id = link.disaster_id" + " WHERE link.geofib = '" + params.get("county") + "';");
                        rs.next();
                        StringBuilder sb = new StringBuilder();
                        while (rs.next()) {
                            sb.append(rs.getString(1)).append("=").append(rs.getInt(2)).append(',');
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
                        ResultSet rs = stmt.executeQuery("SELECT name, geofib FROM county WHERE county.state = '" + params.get("state") + "';");
                        rs.next();
                        StringBuilder sb = new StringBuilder();
                        while (rs.next()) {
                            sb.append(rs.getString(1)).append("=").append(rs.getInt(2)).append(",");
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
            try {
                he.sendResponseHeaders(200, response.length());
            } catch (IOException e) {
                e.printStackTrace();
            }
            OutputStream os = he.getResponseBody();
            try {
                os.write(response.getBytes());
                System.out.println("Wrote: " + response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("stop");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
