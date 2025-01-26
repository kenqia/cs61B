package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blobs implements Serializable {
    private Blob topBlob;


    public String getFile(){
        return this.fileName;
    }

    public void add(String name){

    }

    public class Blob implements Serializable{
        String fileName;
        Blob left;
        Blob right;
    }


}
