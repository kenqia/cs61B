package gitlet;

import java.io.File;

import static gitlet.Utils.join;
import static gitlet.Utils.plainFilenamesIn;

public class Branch {
    private String name;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static File BRANCH_DIR;
    public Branch(String name){
        this.name = name;
        BRANCH_DIR = join(GITLET_DIR, name);
    }
    public String getname(){
        return this.name;
    }

    public void commit(Commit want){
        
    }
}
