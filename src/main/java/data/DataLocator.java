package data;

import java.io.RandomAccessFile;

/**
 * Created by mkhanwalkar on 11/29/15.
 */
public class DataLocator
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
