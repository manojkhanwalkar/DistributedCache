package client;

import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;

/**
 * Created by mkhanwalkar on 11/27/15.
 */
public class NodeClient {

    public Response update(Request request)
    {

        System.out.println(this);
        System.out.println(JSONUtil.getJSONString(request));
        Response response = new Response();
        response.setType(Type.UpdateResult);
        response.setRequestId(request.getId());
        response.setKey(request.getKey());
        response.setValue(request.getValue());

        return response ;
    }

    public Response query(Request request)
    {
        return null ;
    }


    String hostName ;
    int port ;
   // private String config ;

    public void init()
    {


    }

    NodeClient(String config)
    {
        String[] tmp = config.split(":");
        hostName = tmp[0];
        port = Integer.valueOf(tmp[1]);
    }
}
