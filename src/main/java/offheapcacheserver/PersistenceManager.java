package offheapcacheserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.DataContainer;
import data.DataLocator;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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



        //TODO - unmap method needed
        //TODO - map a new file when old one full is needed .
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            int len ;

            while ((  len = raf.readInt())!=0)
            {
                DataLocator dl = new DataLocator();
                dl.setFile(raf);
                dl.setFilePos((int)(raf.getFilePointer()-4));
                byte[] b = new byte[len];
                raf.read(b);
                String s = new String(b);
                DataContainer dc = mapper.readValue(s, DataContainer.class);
                cacheService.recover(dc.getKey(),dl);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    RandomAccessFile raf = null;
    long length =  500;
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

               raf = new RandomAccessFile(dirName+fileName, "rw");

               out = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void destroy()
    {

    }

    public synchronized String read(DataLocator dl)
    {
        RandomAccessFile file = dl.getFile();
        try {
            file.seek(dl.getFilePos());
            int len = file.readInt();
            byte[] b = new byte[len];
            file.read(b);
            String s = new String(b);
            DataContainer dc = mapper.readValue(s, DataContainer.class);
            return dc.getValue();

        } catch (Exception e) {
            e.printStackTrace();
            return null ;
        }
    }


    public synchronized DataLocator write(final String key , final String value)
    {

        //TODO - will change if a new map is required .

        DataContainer dc = new DataContainer(key,value);

        try {
            String s = mapper.writeValueAsString(dc);
            byte[] bytes = s.getBytes(Charset.forName("US-ASCII"));

            if (out.remaining()<bytes.length+4)
            {

                destroyBuffer(out);
                initCurrentFile();
                System.out.println("Mapping new file ");
            }

            DataLocator dl = new DataLocator();
            dl.setFile(raf);
            dl.setFilePos(out.position());
            out.putInt(bytes.length);
                out.put(bytes);

                out.force();
            return dl ;

               } catch (Exception e) {
            e.printStackTrace();
            return null;
        }



    }

    public static void destroyBuffer(Buffer buffer) {
        if(buffer.isDirect()) {
            try {
                if(!buffer.getClass().getName().equals("java.nio.DirectByteBuffer")) {
                    Field attField = buffer.getClass().getDeclaredField("att");
                    attField.setAccessible(true);
                    buffer = (Buffer) attField.get(buffer);
                }

                Method cleanerMethod = buffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(buffer);
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.setAccessible(true);
                cleanMethod.invoke(cleaner);
            } catch(Exception e) {
                throw new RuntimeException("Could not destroy direct buffer " + buffer, e);
            }
        }
    }



}

