package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

/**
 * 存储Branch分支信息
 */
public class Branch implements Serializable {
    public String name;
    public Commit HEAD;
    public String code;

    public Branch(String name) {
        this.name = name;
        this.code = Utils.sha1(name);
    }

    public Branch(String name, Commit root) {
        this.name = name;
        this.code = Utils.sha1(name);
        this.HEAD = root;
    }


    public Commit getSplitPoint(Branch given){
        Commit now = this.HEAD;
        while (now != null) {
            Commit give = given.HEAD;
            while (give != null) {
                if (give.getHashCode().equals(now.getHashCode())) {
                    return give;
                }
                give = give.getParent();
            }
            now = now.getParent();
        }
        return null;
    }

}
