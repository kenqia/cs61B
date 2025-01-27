package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

import static gitlet.Utils.*;


/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  kenqia
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    private Metadata metadata;
    private Commit parent;
    private Commit parentMerge;
    private Blobs file;
    public Commit(Metadata data , Commit first , Commit second , Blobs filehere){
     this.metadata = data;
     this.parent = first;
     this.parentMerge = second;
     this.file = filehere;
    }

    /** The message of this Commit. */
    private String message;


    public void loadingCommit(){

        /** 获取hashcode 与存储路径*/
        String hash = Utils.sha1(this.toString());
        String index = hash.substring(0 , 2);
        File whereCommiting = join(Repository.GITLET_DIR , "commits");
        /** 创建相关文件夹 文件 */
        join(whereCommiting , index).mkdir();
        try {
            join(join(whereCommiting, index), hash.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /** 写入commit信息 */
        writeObject(join(join(whereCommiting, index), hash.substring(2)) , this );

        /** 更改HEADBRANCH 文件信息*/
        Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
        nowBranch.HEAD = this;
        writeObject(join(Repository.GITLET_DIR , "HeadBranch") , nowBranch);
    }

    public void checkStage(){
        /** remake and add */
        Stage nowStage = readObject(join(Repository.GITLET_DIR , "StageFile") , Stage.class );
        this.file = nowStage.check(this.getBlob());
    }


    public Blobs getBlob(){
        return this.file;
    }
}
