package handler;

import cache.CacheService;
import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;
import server.Server;
import socket.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public class ClientHandler implements  Handler
{

    Socket socket ;
    public ClientHandler(Socket socket)
    {
        this.socket = socket ;
    }

    public ClientHandler()
    {

    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            try {

                BufferedReader input =
                        new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String  s = input.readLine();
                    System.out.println(s);
                    if (s==null)
                        break;

                    Request request = JSONUtil.getRequestFromJSONString(s);

                    Response response = new Response();
                    response.setKey(request.getKey());
                    response.setRequestId(request.getId());
                    CacheService cacheService = (CacheService) Server.getService("CacheService");
                    switch(request.getType())
                    {
                        case Update:
                            response.setType(Type.UpdateResult);
                            cacheService.update(request.getKey(),request.getValue());
                            break;
                        case Query:
                            response.setType(Type.QueryResult);
                            String s1 = cacheService.query(request.getKey());
                            response.setValue(s1);
                            break ;


                    }


                     out.println(JSONUtil.getJSONString(response));
                }
            }
            finally {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
