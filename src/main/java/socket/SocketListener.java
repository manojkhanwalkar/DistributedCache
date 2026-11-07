package socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by mkhanwalkar on 11/27/15.
 *
 * Accepts client connections and hands each one to a {@link Handler} running on a
 * bounded thread pool. Connections beyond {@code maxConnections} are refused rather
 * than spawning an unbounded number of threads.
 */
public class SocketListener {

    public static final String DEFAULT_BIND_ADDRESS = "127.0.0.1";
    public static final int DEFAULT_MAX_CONNECTIONS = 100;
    public static final int DEFAULT_READ_TIMEOUT_MILLIS = 300_000;

    private volatile ServerSocket serverSocket;
    private volatile boolean running;
    private ThreadPoolExecutor workers;

    String handlerType;
    String bindAddress = DEFAULT_BIND_ADDRESS;
    int maxConnections = DEFAULT_MAX_CONNECTIONS;
    int readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;

    public void init(final int port)
    {
        final Class<? extends Handler> handlerClass;
        try {
            handlerClass = Class.forName(handlerType).asSubclass(Handler.class);
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName(bindAddress), port));
        } catch (ClassNotFoundException | ClassCastException | IOException e) {
            throw new IllegalStateException("Could not start listener on " + bindAddress + ":" + port, e);
        }

        workers = new ThreadPoolExecutor(0, maxConnections, 60, TimeUnit.SECONDS, new SynchronousQueue<>());
        running = true;

        Thread t = new Thread(()->{
            while (running) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    if (running) {
                        // transient accept failure (e.g. too many open files): keep serving
                        System.err.println("Accept failed: " + e);
                        continue;
                    }
                    break;
                }
                try {
                    socket.setSoTimeout(readTimeoutMillis);
                    Handler handler = handlerClass.newInstance();
                    handler.setSocket(socket);
                    workers.execute(handler);
                } catch (RejectedExecutionException e) {
                    System.err.println("Connection limit (" + maxConnections + ") reached, refusing "
                            + socket.getRemoteSocketAddress());
                    closeQuietly(socket);
                } catch (Exception e) {
                    System.err.println("Could not handle connection: " + e);
                    closeQuietly(socket);
                }
            }
        }, "SocketListener-" + port);

        t.start();
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    public void destroy() {
        running = false;
        try {
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException ignored) {
        }
        if (workers != null)
            workers.shutdownNow();
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }
}
