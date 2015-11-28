package cacheserver;

import server.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CacheService implements Service {



    String name ;

    ConcurrentMap<String,String> keyValueData = new ConcurrentHashMap<>();

    PersistenceManager manager ;

    public PersistenceManager getManager() {
        return manager;
    }

    public void setManager(PersistenceManager manager) {
        this.manager = manager;
    }

    public void recover(String key, String value)
    {
        keyValueData.put(key,value);

    }

    public void update(String key , String value)
    {
        keyValueData.put(key,value);
        manager.write(key,value);
    }

    public String query(String key)
    {
        return keyValueData.get(key);
    }

    //TODO - Add persistence manager for file here as dependency . Async write to the file using executor .


    @Override
    public void init() {

            manager.init(this);
        System.out.println(keyValueData);

}

    @Override
    public void destroy() {

        System.out.println("In cache service destroy ");
        manager.destroy();

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
