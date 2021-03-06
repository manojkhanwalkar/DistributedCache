package client;

import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by mkhanwalkar on 11/27/15.
 */
public class NodeClient {

    public Response update(Request request)
    {
        return process(request);
    }

    private synchronized String sendNRecv(String s)
    {
        try {
            out.println(s);
            return input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }



    }

    private Response process(Request request)
    {
        String answer = sendNRecv(JSONUtil.getJSONString(request));
        Response response = JSONUtil.getResponseFromJSONString(answer);
        return response ;

    }

    public Response query(Request request)
    {
        return process(request);
    }


    String hostName ;
    int port ;
   // private String config ;

    Socket s ;
    BufferedReader input ;
    PrintWriter out ;

    public void init()
    {

        try {
            s = new Socket(hostName, port);
            input =
                   new BufferedReader(new InputStreamReader(s.getInputStream()));

            out =
                   new PrintWriter(s.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    NodeClient(String config)
    {
        String[] tmp = config.split(":");
        hostName = tmp[0];
        port = Integer.valueOf(tmp[1]);

        init();
    }
}
