package socket;

import server.Service;
import socket.SocketListener;

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
