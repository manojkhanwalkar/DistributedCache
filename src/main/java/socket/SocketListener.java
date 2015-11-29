package socket;

import cache.CacheService;
import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;
import server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by mkhanwalkar on 11/27/15.
 */
public class SocketListener {

    public void init(final int port)
    {
        Thread t = new Thread(()->{


            try {
                ServerSocket listener = new ServerSocket(port);
                try {
                    while(true) {
                        Socket socket = listener.accept();
                        Thread t1 = new Thread(new ClientHandler(socket));
                        t1.start();
                    }
                }
                finally {
                    listener.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        t.start();



    }


    public void destroy() {


    }
}

class ClientHandler implements Runnable
{

    Socket socket ;
    public ClientHandler(Socket socket)
    {
        this.socket = socket ;
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
                    CacheService cacheService = (CacheService)Server.getService("CacheService");
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
