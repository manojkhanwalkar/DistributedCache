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

    // Interface to listen on. Defaults to loopback; set to a specific interface
    // address (or 0.0.0.0) only on a trusted network - the protocol has no authentication.
    String bindAddress = SocketListener.DEFAULT_BIND_ADDRESS;

    int maxConnections = SocketListener.DEFAULT_MAX_CONNECTIONS;

    int readTimeoutMillis = SocketListener.DEFAULT_READ_TIMEOUT_MILLIS;

    public String getBindAddress() {
        return bindAddress;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getReadTimeoutMillis() {
        return readTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    @Override
    public void init() {

        listener = new SocketListener();
        listener.setHandlerType(handlerType);
        listener.setBindAddress(bindAddress);
        listener.setMaxConnections(maxConnections);
        listener.setReadTimeoutMillis(readTimeoutMillis);
        listener.init(port);


}

    @Override
    public void destroy() {

        if (listener != null)
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
