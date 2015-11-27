package cacheserver;

import server.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ClientListenerService implements Service {



    String name ;


    @Override
    public void init() {

        Thread t = new Thread(()->{


            try {
                ServerSocket listener = new ServerSocket(10003);
                try {
                        Socket socket = listener.accept();
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

                                out.println(new Date().toString());
                            }
                        } finally {
                            socket.close();
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

    @Override
    public void destroy() {




    }

    @Override
    public void setName(String s) {

        name = s;

    }

    @Override
    public String getName() {
        return name;
    }
}
