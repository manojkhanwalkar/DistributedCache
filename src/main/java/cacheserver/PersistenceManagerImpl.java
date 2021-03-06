package cacheserver;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void init(CacheServiceImpl cacheService)
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
        try {
            FileReader reader = new FileReader(file);
            BufferedReader bReader = new BufferedReader(reader);
            while (true)
            {
                String s =  bReader.readLine();
                if (s!=null) {
                    DataContainer dc = mapper.readValue(s,DataContainer.class);
                    cacheService.recover(dc.getKey(),dc.getValue());
                }
                else
                    break ;
            }

            bReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initCurrentFile()
    {
        FileWriter fw = null;
        String fileName = UUID.randomUUID().toString();
        try {
            fw = new FileWriter(dirName+fileName);
            bw = new BufferedWriter(fw);
        } catch (Exception e) {
            e.printStackTrace();
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

