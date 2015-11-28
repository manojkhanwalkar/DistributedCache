package cacheserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by mkhanwalkar on 11/28/15.
 */
public class PersistenceManager {

    String dirName ;

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    BufferedWriter bw ;

    static         ObjectMapper mapper = new ObjectMapper();

    public void init()
    {

        FileWriter fw = null;
        String fileName = UUID.randomUUID().toString();
        try {
            fw = new FileWriter(dirName+fileName);
             bw = new BufferedWriter(fw);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void destroy()
    {
        try {
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // design an iterator to give to the application to keep reading records till there are none .

    public void write(String key , String value)
    {
        DataContainer dc = new DataContainer(key,value);

        try {
            String s = mapper.writeValueAsString(dc);
            bw.write(s);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /*
       ObjectMapper mapper = new ObjectMapper();

        FileWriter fw = new FileWriter("/tmp/t.json");
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i=0;i<10;i++)
        {
            Request request = new Request();
            request.setKey("Hello"+i);
            request.setValue("World");
            request.setType(Type.Update);

            bw.write(JSONUtil.getJSONString(request));
            bw.newLine();


        }


     */
}

class DataContainer
{
    String key ;
    String value ;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DataContainer(String key, String value) {
        this.key = key ;
        this.value = value ;

    }
}
