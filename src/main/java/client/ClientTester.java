package client;

import data.JSONUtil;
import data.Request;
import data.Response;
import data.Type;

/**
 * Created by mkhanwalkar on 11/26/15.
 */
public class ClientTester {

    public static void main(String[] args) throws Exception {

     //   DCClient client = DCClient.getInstance("localhost:100001,localhost:1002,localhost:1003");
        DCClient client = DCClient.getInstance("localhost:10003,localhost:10004");

       for (int i=0;i<10;i++)
        {
            Request request = new Request();
            request.setKey("Hello"+i);
            request.setValue("World");
            request.setType(Type.Update);

            Response response = client.update(request);

            System.out.println(JSONUtil.getJSONString(response));

           //Thread.sleep(1000);
        }

       for (int i=0;i<10;i++)
        {
            Request request = new Request();
            request.setKey("Hello"+i+i);
          // request.setValue("World");
            request.setType(Type.Query);

            Response response = client.update(request);

            System.out.println(JSONUtil.getJSONString(response));

           // Thread.sleep(2000);
        }

    }
}
