package client;

import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;

import javax.xml.soap.Node;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkhanwalkar on 11/26/15.
 */
public class DCClient {

    private DCClient()
    {

    }

    public Response update(Request request)
    {
        int node = Math.abs(request.getKey().hashCode()%clusterSize);
        NodeClient client = clients.get(node);
        try {
            return client.update(request);
        } catch (Exception e)
        {
            System.out.println("Primary node not found , trying secondary");
            NodeClient secclient = getSecondaryNode(node);
            return secclient.update(request);
        }

    }

    private NodeClient getSecondaryNode(int primary)
    {
        primary++;
        if (primary==clusterSize)
            primary = 0;

        return clients.get(primary);
    }

    public Response query(Request request)
    {
        return null ;
    }


    private String config ;

    int clusterSize = -1 ;
    List<NodeClient> clients = new ArrayList<>();

    private void init(String config)
    {
        //"localhost:100001,localhost:1002,localhost:1003"

        String[] tmp = config.split(",");
        clusterSize = tmp.length;
        for (String s : tmp)
            clients.add(new NodeClient(s));
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
