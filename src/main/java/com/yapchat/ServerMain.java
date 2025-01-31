package com.yapchat;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.sun.net.httpserver.HttpServer;

public class ServerMain {
    public static void  main(String[] args){

        int port = 0;

        Scanner sc = new Scanner(System.in);

        System.out.println("Give port number");
        try{
            port = sc.nextInt();
        }
        catch(InputMismatchException ime){
            System.out.println("Wrong format, using set default");
        }
        sc.close();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            server.createContext("/", new ServerHandler());

            server.setExecutor(null);
            server.start();

            System.out.println("Server running on: " + port);
        }
        catch (IOException IOE) {
            System.out.println(IOE.getMessage());
        }


    }
}
