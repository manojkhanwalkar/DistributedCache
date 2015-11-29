package offheapcacheserver;

import server.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CacheService implements Service {



    String name ;

    ConcurrentMap<String,DataLocator> keyValueData = new ConcurrentHashMap<>();

    PersistenceManager manager ;

    public PersistenceManager getManager() {
        return manager;
    }

    public void setManager(PersistenceManager manager) {
        this.manager = manager;
    }

    public void recover(String key, String value)
    {
//        keyValueData.put(key,value);

    }

    public void update(String key , String value)
    {
        DataLocator dl = manager.write(key, value);
        keyValueData.put(key,dl);
    }

    public String query(String key)
    {

        DataLocator dl =  keyValueData.get(key);
        // get value from the file
        return null ;
    }



    @Override
    public void init() {

            manager.init(this);
        System.out.println(keyValueData);

}

    @Override
    public void destroy() {

        System.out.println("In off heap cache service destroy ");
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
