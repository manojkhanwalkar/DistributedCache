package offheapcacheserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.JSONUtil;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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


    static         ObjectMapper mapper = new ObjectMapper();

    CacheService cacheService;

    public void init(CacheService cacheService)
    {
        this.cacheService = cacheService;
        recoverData();
        initCurrentFile();


    }

    private void recoverData()
    {
        File directory = new File(dirName);

        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile()) {
                recoverData(file);
            }
        }
    }

    private void recoverData(File file)
    {
 /*       try {
            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            while (true)
            {
                String s =  bReader.readLine();
                if (s!=null) {
                    DataContainer dc = mapper.readValue(s, DataContainer.class);
                    cacheService.recover(dc.getKey(),dc.getValue());
                }
                else
                    break ;
            }

            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // TODO - need to create DL and not DC in the recovery .
        //TODO - need a method to read the data for a given key
        //TODO - unmap method needed
        //TODO - map a new file when old one full is needed .
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            int len ;
            while ((  len = raf.readInt())!=0)
            {
                byte[] b = new byte[len];
                raf.read(b);

                String s = new String(b);
                System.out.println(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    RandomAccessFile raf = null;
    long length =  0x8FFFFFF;
    MappedByteBuffer out = null;

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    private void initCurrentFile()
    {

        String fileName = UUID.randomUUID().toString();
        try {

               out = new RandomAccessFile(dirName+fileName, "rw")
                    .getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void destroy()
    {

    }


    public synchronized DataLocator write(final String key , final String value)
    {

        //TODO - will change if a new map is required .
        //TODO - needs proper sync
        DataLocator dl = new DataLocator();

        dl.setFile(raf);
        dl.setFilePos(out.position());

        DataContainer dc = new DataContainer(key,value);

        try {
            String s = mapper.writeValueAsString(dc);
              byte[] bytes = s.getBytes(Charset.forName("US-ASCII"));

                out.putInt(bytes.length);
                out.put(bytes);

                out.force();
               } catch (Exception e) {
            e.printStackTrace();
        }

        return dl;


    }



}

class DataLocator
{
    RandomAccessFile file ;

    int filePos ;

    public int getFilePos() {
        return filePos;
    }

    public void setFilePos(int filePos) {
        this.filePos = filePos;
    }

    public RandomAccessFile getFile() {
        return file;
    }

    public void setFile(RandomAccessFile file) {
        this.file = file;
    }
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

    public DataContainer()
    {

    }
}
