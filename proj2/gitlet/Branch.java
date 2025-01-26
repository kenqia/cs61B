package gitlet;

import java.io.Serializable;

public class Branch implements Serializable {
    String name;
    Commit HEAD;
    public Branch(String name){
        this.name = name;
    }
}
