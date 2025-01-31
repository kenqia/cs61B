package gitlet;

import java.io.Serializable;
/** 存储Branch分支信息 */
public class Branch implements Serializable {
    String name;
    Commit HEAD;
    String code;
    public Branch(String name){
        this.name = name;
        this.code = Utils.sha1(name);
    }

    public Branch(String name , Commit root){
        this.name = name;
        this.code = Utils.sha1(name);
        this.HEAD = root;
    }

}
