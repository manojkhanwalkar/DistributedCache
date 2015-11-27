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

    SocketListener listener ;

    int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void init() {

        listener = new SocketListener();
        listener.init(port);


}

    @Override
    public void destroy() {

        listener.destroy();


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
