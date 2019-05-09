package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DataGetHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        // FIXME real data

        String response = "";
        String query = he.getRequestURI().toString();
        if (query.length() == 0) {
            // empty, provide state
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
            } else if (params.keySet().contains("category")) {
                // requesting category names
            } else if (params.keySet().contains("disaster")) {
                // requesting categories
            } else if (params.keySet().contains("county")) {
                // requesting disasters
            } else if (params.keySet().contains("state")) {
                // requesting counties
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