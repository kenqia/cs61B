package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

import static gitlet.Repository.CWD;
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

    public Metadata metadata;
    private Commit parent;
    private Commit parentMerge;
    private Blobs file;
    private String hashCode;
    public Commit(Metadata data , Commit first , Commit second , Blobs filehere){
     this.metadata = data;
     this.parent = first;
     this.parentMerge = second;
     this.file = filehere;
     this.hashCode = Utils.sha1(this.getBlob().toString() + this.metadata.logMessage + this.metadata.timestamp);
    }

    /** The message of this Commit. */
    private String message;


    public void loadingCommit(){

        /** 获取hashcode 与存储路径*/
        String index = this.hashCode.substring(0 , 2);
        File whereCommiting = join(Repository.GITLET_DIR , "commits");
        /** 创建相关文件夹 文件 */
        join(whereCommiting , index).mkdir();
        try {
            join(join(whereCommiting, index), this.hashCode.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /** 写入commit信息 */
        writeObject(join(join(whereCommiting, index), this.hashCode.substring(2)) , this );

        /** 更改HEADBRANCH 文件信息*/
        Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
        nowBranch.HEAD = this;
        Repository.addBranch(nowBranch);
        writeObject(join(Repository.GITLET_DIR , "HeadBranch") , nowBranch);
    }

    public void checkStage(){
        /** remake and add */
        Stage nowStage = readObject(join(Repository.GITLET_DIR , "StageFile") , Stage.class );
        this.file = nowStage.check(this.getBlob());
    }
    /**把所有这个commit跟踪的有关路径的文件删除 */
    public void delete(){
        deleteTime(this.getBlob().getRoot());
    }

    private void deleteTime(Blobs.Blob x){
        if(x == null) return;
        join(CWD , x.getName()).delete();
        deleteTime(x.getLeft());
        deleteTime(x.getRight());
    }
    /** 检查一下这个commit要checkout过来的文件有没有被全部追踪 */
    public void checkTrace(){
        Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
        checkTraceTime(this.getBlob().getRoot() , nowBranch);
    }
    public void checkTraceTime(Blobs.Blob x , Branch e){
        if (x == null) return;
        if(!e.HEAD.getBlob().searchExist(x.getName())){
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        checkTraceTime(x.getLeft() , e);
        checkTraceTime(x.getRight() , e);
    }

    /**把所有这个commit跟踪的有关文件导入进来*/
    public void get(){
        getTime(this.getBlob().getRoot());
    }

    private void getTime(Blobs.Blob x){
        if(x == null) return;
        try {
            if (!join(CWD , x.getName()).exists()) {
                if (x.getHashCode().equals("0000000000000000000000000000000000000000")) {
                    return;
                }
                join(CWD, x.getName()).createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(CWD , x.getName()) , Blobs.getContents(x));
        getTime(x.getLeft());
        getTime(x.getRight());
    }


    public Blobs getBlob(){
        return this.file;
    }

    public Commit getParent(){
        return this.parent;
    }

    public Commit getParentMerge(){
        return this.parentMerge;
    }

    public String getHashCode(){
        return this.hashCode;
    }
}
