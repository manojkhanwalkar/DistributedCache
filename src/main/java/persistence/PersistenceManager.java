package persistence;

import cacheserver.CacheServiceImpl;
import data.DataLocator;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public interface PersistenceManager {

    public  DataLocator write(final String key , final String value);

    public void destroy();

    public default void init(CacheServiceImpl service)
    {

    }

    public default void init(offheapcacheserver.CacheServiceImpl service)
    {

    }

    public default String read(DataLocator dl)
    {
        return null;
    }
}
