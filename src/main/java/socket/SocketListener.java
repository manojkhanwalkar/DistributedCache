package socket;

import handler.ClientHandler;

import java.io.IOException;
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
                        Handler handler = (Handler) Class.forName(handlerType).newInstance();
                        handler.setSocket(socket);
                        //Thread t1 = new Thread(new ClientHandler(socket));
                        Thread t1 = new Thread(handler);
                        t1.start();
                    }
                }
                finally {
                    listener.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        t.start();



    }


    public void destroy() {


    }

    String handlerType;

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;

    }
}

