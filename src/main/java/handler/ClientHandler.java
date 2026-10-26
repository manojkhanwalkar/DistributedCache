package handler;

import cache.CacheService;
import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;
import server.Server;
import socket.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public class ClientHandler implements  Handler
{
    /** Maximum length (in chars) of one JSON request line. Longer lines close the connection. */
    public static final int MAX_LINE_LENGTH = 1024 * 1024;

    Socket socket ;
    public ClientHandler(Socket socket)
    {
        this.socket = socket ;
    }

    public ClientHandler()
    {

    }

    @Override
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            try {

                BufferedReader input =
                        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String  s = readLine(input, MAX_LINE_LENGTH);
                    if (s==null)
                        break;

                    Request request = JSONUtil.getRequestFromJSONString(s);
                    if (!isValid(request)) {
                        System.err.println("Invalid request from " + socket.getRemoteSocketAddress()
                                + ", closing connection");
                        break;
                    }

                    Response response = new Response();
                    response.setKey(request.getKey());
                    response.setRequestId(request.getId());
                    CacheService cacheService = (CacheService) Server.getService("CacheService");
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
                        default:
                            break;
                    }


                     out.println(JSONUtil.getJSONString(response));
                }
            }
            finally {
                socket.close();
            }
        } catch (SocketTimeoutException e) {
            // idle client: connection closed above
        } catch (IOException e) {
            System.err.println("Connection error from " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error handling request from " + socket.getRemoteSocketAddress() + ": " + e);
        }


    }

    static boolean isValid(Request request)
    {
        if (request == null || request.getKey() == null || request.getType() == null)
            return false;
        switch (request.getType()) {
            case Update:
                return request.getValue() != null;
            case Query:
                return true;
            default:
                return false;
        }
    }

    /**
     * Reads one line terminated by '\n' (a trailing '\r' is stripped), like
     * BufferedReader.readLine, but fails instead of buffering more than maxLength chars.
     * Returns null at end of stream.
     */
    static String readLine(Reader in, int maxLength) throws IOException
    {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\n') {
                int len = sb.length();
                if (len > 0 && sb.charAt(len - 1) == '\r')
                    sb.setLength(len - 1);
                return sb.toString();
            }
            if (sb.length() >= maxLength)
                throw new IOException("Request line exceeds " + maxLength + " characters");
            sb.append((char) c);
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
