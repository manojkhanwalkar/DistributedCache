package persistence;

import cacheserver.CacheService;
import data.DataLocator;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public interface PersistenceManager {

    public  DataLocator write(final String key , final String value);

    public void destroy();

    public default void init(CacheService service)
    {

    }

    public default void init(offheapcacheserver.CacheService service)
    {

    }

    public default String read(DataLocator dl)
    {
        return null;
    }
}
