package data;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public class DataContainer
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

    public DataContainer()
    {

    }
}
