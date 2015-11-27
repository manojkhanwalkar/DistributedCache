package server;

public class ServerShutDownHook implements Runnable {

    Server server;
    public ServerShutDownHook(Server s)
    {
        server=s;
    }

    public void run()
    {
        System.out.println("Shutting down services");

        server.stop();

        server.destroy();
    }
}