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


    @Override
    public void init() {


}

    @Override
    public void destroy() {



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
