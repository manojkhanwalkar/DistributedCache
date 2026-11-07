package cacheserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import persistence.DataDirectory;
import persistence.PersistenceManager;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import data.*;

/**
 * Created by mkhanwalkar on 11/28/15.
 */
public class PersistenceManagerImpl  implements PersistenceManager {

    String dirName ;

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    BufferedWriter bw ;

    static         ObjectMapper mapper = new ObjectMapper();

    CacheServiceImpl cacheService;

    ExecutorService service = Executors.newFixedThreadPool(1);

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
        try (BufferedReader bReader = new BufferedReader(new FileReader(file))) {
            while (true)
            {
                String s =  bReader.readLine();
                if (s!=null) {
                    DataContainer dc = mapper.readValue(s,DataContainer.class);
                    if (dc.getKey() != null && dc.getValue() != null)
                        cacheService.recover(dc.getKey(),dc.getValue());
                }
                else
                    break ;
            }
        } catch (IOException e) {
            System.err.println("Could not recover data from " + file + ": " + e.getMessage());
        }
    }

    private void initCurrentFile()
    {
        FileWriter fw = null;
        String fileName = UUID.randomUUID().toString();
        try {
            fw = new FileWriter(DataDirectory.createDataFile(dataDir, fileName));
            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create data file in " + dataDir, e);
        }

    }

    public void destroy()
    {
        service.shutdown();
        try {
            service.awaitTermination(1, TimeUnit.HOURS);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // design an iterator to give to the application to keep reading records till there are none .

    public DataLocator write(final String key , final String value)
    {

        service.submit(()->{
        DataContainer dc = new DataContainer(key,value);

        try {
            String s = mapper.writeValueAsString(dc);
            bw.write(s);
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

        });
        return null;
    }



}

