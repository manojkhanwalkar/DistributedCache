package data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by mkhanwalkar on 11/26/15.
 */
public class JSONUtil {

    static ObjectMapper mapper = new ObjectMapper();

    public static String getJSONString(Request request)
    {

        try {
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Request getRequestFromJSONString(String str)
    {
        try {
            Request request = mapper.readValue(str,Request.class);
            return request ;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getJSONString(Response response)
    {

        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Response getResponseFromJSONString(String str)
    {
        try {
            Response response = mapper.readValue(str,Response.class);
            return response ;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
