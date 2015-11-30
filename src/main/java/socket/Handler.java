package socket;

import java.net.Socket;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public interface Handler extends Runnable {

    public void setSocket(Socket socket);


}
