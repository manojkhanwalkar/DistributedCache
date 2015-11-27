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

        Response response = null;
        try {
            System.out.println(JSONUtil.getJSONString(request));
            response = new Response();
            response.setType(Type.UpdateResult);
            response.setRequestId(request.getId());
            response.setKey(request.getKey());
            response.setValue(request.getValue());

            out.println(JSONUtil.getJSONString(request));

            String answer = input.readLine();

            System.out.println(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return response ;
    }

    public Response query(Request request)
    {
        return null ;
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
