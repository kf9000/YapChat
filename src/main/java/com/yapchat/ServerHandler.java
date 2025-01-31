package com.yapchat;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class ServerHandler implements HttpHandler {

    private final String PATH = "messages.json";
    private String response;
    private int code;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        System.out.println(method);
        switch (method) {
            case "POST": {
                doPost(exchange);
                break;
            }
            case "GET": {
                doGet();
                break;
            }
            default:
                response = "Not available";
                code = 405;

        }

        exchange.sendResponseHeaders(code, response.getBytes(StandardCharsets.UTF_8).length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(StandardCharsets.UTF_8));
        os.close();

    }

    private void doPost(HttpExchange exchange) throws IOException {
        String oldMessages = "";

        InputStreamReader inputReader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader bufferReader = new BufferedReader(inputReader);

        JSONObject receivedMessage = new JSONObject(bufferReader.readLine());

        long time = System.currentTimeMillis();

        receivedMessage.put("time", time);


        try{
            oldMessages = new String((Files.readAllBytes(Paths.get(PATH))));
        }
        catch (IOException ioe){
            System.out.println("Error with file: " + PATH);
        }

        JSONArray messageContents = new JSONArray(oldMessages);

        messageContents.put(receivedMessage);

        try{
            FileWriter file = new FileWriter(PATH);
            file.write(messageContents.toString());
            file.close();
        }
        catch (FileNotFoundException fnfe){
            System.out.println("File not found");
        }
        int lastMessageIndex = messageContents.toList().size() - 1;
        JSONObject latestMessage = messageContents.getJSONObject(lastMessageIndex);

        String alias = latestMessage.get("alias").toString();
        String message = latestMessage.get("message").toString();
        long timeStamp = Long.parseLong(latestMessage.get("time").toString());

        LocalDateTime localTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), TimeZone.getDefault().toZoneId());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

        System.out.println("Alias: " + alias);
        System.out.println("Message: " + message);
        System.out.println("time: " + localTime.format(formatter));

        response = "Alias: " + alias +"\nMessage: " + message + "\ntime: " + localTime.format(formatter);

        inputReader.close();
        code = 200;
    }

    private void doGet(){
        String messages = "Something went wrong, lol";

        try{
            messages = new String((Files.readAllBytes(Paths.get(PATH))));
        }
        catch (IOException ioe){
            System.out.println("Error with file: " + PATH);
        }

        response = messages;
        code = 200;
    }

}
