package cache;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public interface CacheService {

    public void update(String key , String value);

    public String query(String key);
}
