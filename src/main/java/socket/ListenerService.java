package socket;

import server.Service;
import socket.SocketListener;

public class ListenerService implements Service {



    String name ;

    SocketListener listener ;

    int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    String handlerType ;

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    @Override
    public void init() {

        listener = new SocketListener();
        listener.init(port);
        listener.setHandlerType(handlerType);


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
