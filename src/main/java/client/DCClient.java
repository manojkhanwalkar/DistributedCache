package client;

import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;

/**
 * Created by mkhanwalkar on 11/26/15.
 */
public class DCClient {

    private DCClient()
    {

    }

    public Response update(Request request)
    {

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


    private String config ;

    private void init(String config)
    {

    }

    static class Holder
    {
        static DCClient factory = new DCClient();
    }

    public static DCClient getInstance(String config)
    {
        Holder.factory.init(config);
        return Holder.factory;

    }




}
