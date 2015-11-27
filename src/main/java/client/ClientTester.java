package client;

import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;

/**
 * Created by mkhanwalkar on 11/26/15.
 */
public class ClientTester {

    public static void main(String[] args) {

        DCClient client = DCClient.getInstance("localhost:100001,localhost:1002,localhost:1003");

        Request request = new Request();
        request.setKey("Hello");
        request.setValue("World");
        request.setType(Type.Uodate);

        Response response = client.update(request);

        System.out.println(JSONUtil.getJSONString(response));
    }
}
