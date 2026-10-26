package offheapcacheserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import data.DataContainer;
import data.DataLocator;
import persistence.DataDirectory;
import persistence.PersistenceManager;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Created by mkhanwalkar on 11/28/15.
 */
public class PersistenceManagerImpl implements PersistenceManager{

    String dirName ;

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }


    static         ObjectMapper mapper = new ObjectMapper();

    CacheServiceImpl cacheService;

    File dataDir ;

    public void init(CacheServiceImpl cacheService)
    {
        this.cacheService = cacheService;
        try {
            dataDir = DataDirectory.prepare(dirName);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot use data directory " + dirName, e);
        }
        recoverData();
        initCurrentFile();


    }

    private void recoverData()
    {
        for (File file : DataDirectory.dataFiles(dataDir)) {
            recoverData(file);
        }
    }

    private void recoverData(File file)
    {



        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            int len ;

            while ((  len = raf.readInt())!=0)
            {
                // a record can never be larger than what is left of the mapped file
                if (len < 0 || len > raf.length() - raf.getFilePointer())
                    throw new IOException("Corrupt record length " + len + " in " + file);
                DataLocator dl = new DataLocator();
                dl.setFile(raf);
                dl.setFilePos((int)(raf.getFilePointer()-4));
                byte[] b = new byte[len];
                raf.readFully(b);
                String s = new String(b, StandardCharsets.US_ASCII);
                DataContainer dc = mapper.readValue(s, DataContainer.class);
                if (dc.getKey() != null)
                    cacheService.recover(dc.getKey(),dl);

            }

        } catch (EOFException e) {
            // end of a completely filled file
        } catch (IOException e) {
            System.err.println("Could not recover data from " + file + ": " + e.getMessage());
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

               raf = new RandomAccessFile(DataDirectory.createDataFile(dataDir, fileName), "rw");

               out = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);


        } catch (IOException e) {
            throw new IllegalStateException("Cannot create data file in " + dataDir, e);
        }

    }

    public void destroy()
    {

    }

    public synchronized String read(DataLocator dl)
    {
        if (dl == null)
            return null;
        RandomAccessFile file = dl.getFile();
        try {
            file.seek(dl.getFilePos());
            int len = file.readInt();
            if (len < 0 || len > file.length() - file.getFilePointer())
                throw new IOException("Corrupt record length " + len);
            byte[] b = new byte[len];
            file.readFully(b);
            String s = new String(b, StandardCharsets.US_ASCII);
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
            byte[] bytes = s.getBytes(StandardCharsets.US_ASCII);

            // A record that cannot fit even in an empty file must be rejected up front;
            // otherwise every such request would map (and leak) a new file and then fail.
            if (bytes.length + 4L > length)
                throw new IllegalArgumentException("Record of " + bytes.length
                        + " bytes exceeds the maximum of " + (length - 4) + " bytes");

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

               } catch (IOException e) {
            throw new UncheckedIOException(e);
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
                // Best effort only: on JDK 9+ the cleaner is not reflectively accessible and the
                // mapping is released when the buffer is garbage collected.
            }
        }
    }



}

