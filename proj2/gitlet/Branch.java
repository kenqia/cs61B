package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.readObject;

/** 存储Branch分支信息 */
public class Branch implements Serializable {
    public String name;
    public Commit HEAD;
    public String code;
    public SplitPoint root;

    public Branch(String name){
        this.name = name;
        this.code = Utils.sha1(name);
        this.root = new SplitPoint();
    }

    public Branch(String name , Commit root){
        this.name = name;
        this.code = Utils.sha1(name);
        this.HEAD = root;
        this.root = new SplitPoint();
    }

    public void addMergeInfo(Branch x){
        SplitPoint e = this.root;
        while(e.next != null) {
            e = e.next;
        }
        e.next = new SplitPoint(this.HEAD.getHashCode() , x);
    }

    public SplitPoint getPoint(Branch given){
        SplitPoint x = given.root.next;
        SplitPoint z = this.root.next;
        while(z != null){
            while(x != null){
                if(x.commitCode.equals(z.commitCode)){
                    return x;
                }
                x = x.next;
            }
            z = z.next;
        }
        return null;
    }

    public class SplitPoint implements Serializable{
        private String commitCode;
        private Branch anotherBranch;
        private SplitPoint next = null;

        public SplitPoint(){
            this.commitCode = "0";
            this.anotherBranch = null;
            this.next = null;
        }

        public SplitPoint(String code , Branch another){
            this.commitCode = code;
            this.anotherBranch = another;
            this.next = null;
        }

        public String getCommitCode(){
            return this.commitCode;
        }

        public Commit getCommit(){
            File whereCommit = join(Repository.GITLET_DIR , "commits");
            String code = this.commitCode;
            String item = code.substring(0 , 2);
            String one = code.substring(2);
            File commitNow = join(whereCommit , item);
            return readObject(join(commitNow , one ) , Commit.class);
        }
    }

}
