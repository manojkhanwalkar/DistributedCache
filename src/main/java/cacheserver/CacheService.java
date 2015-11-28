package cacheserver;

import server.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CacheService implements Service {



    String name ;

    ConcurrentMap<String,String> keyValueData = new ConcurrentHashMap<>();

    public void update(String key , String value)
    {
        keyValueData.put(key,value);
    }

    public String query(String key)
    {
        return keyValueData.get(key);
    }

    //TODO - Add persistence manager for file here as dependency . Async write to the file using executor .


    @Override
    public void init() {

        // TODO - recover state from file here


}

    @Override
    public void destroy() {

        System.out.println("In cache service destroy ");
        //TODO - close and flush file here

    }

    @Override
    public void setName(String s) {

        name = s;

    }

    @Override
    public String getName() {
        return name;
    }
}
