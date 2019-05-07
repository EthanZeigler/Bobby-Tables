package edu.tcnj.bobby_tables;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class FieldsGetHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange he) throws IOException {
        // FIXME real data

        String response = "New Jersey=NJ,Pensylvania=PA";
        System.out.println(he.getRequestURI());
        
        Headers responseHeaders = he.getResponseHeaders();
        responseHeaders.add("Access-Control-Allow-Origin", "*");
        responseHeaders.add("Content-type", "application/json");
        he.sendResponseHeaders(200, response.length());    
        OutputStream os = he.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}