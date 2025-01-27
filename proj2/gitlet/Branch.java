package gitlet;

import java.io.Serializable;
/** 存储Branch分支信息 */
public class Branch implements Serializable {
    String name;
    Commit HEAD;
    public Branch(String name){
        this.name = name;
    }
}
