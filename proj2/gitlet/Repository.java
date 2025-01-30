package gitlet;

import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  kenqia
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /* TODO: fill in the rest of this class. */

    public static void init(){
        /**创建各种文件夹*/
        if(!GITLET_DIR.exists()){
            GITLET_DIR.mkdir();
            join(GITLET_DIR , "commits").mkdir();
            join(GITLET_DIR , "stagingArea").mkdir();
            join(GITLET_DIR , "objects").mkdir();
            /**存储删除信息 */
            join(join(GITLET_DIR , "objects") , "00").mkdir();
            try {
                join(join(join(GITLET_DIR , "objects") , "00") , "00000000000000000000000000000000000000").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            join(GITLET_DIR , "branch").mkdir();
        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void add(String name){
        /** 获取要add的文件路径 */
        File theFile = join(CWD , name);
            if(!theFile.exists()) {
                System.out.println("File does not exist.");
                System.exit(0);
            }
        /**获取文件 内容 hashcode */
            String contents = readContentsAsString(theFile);
            String hashCode = sha1(readContents(theFile) + name);
            /** 检查当前commit的文件 , 先获取其Blobs*/
            Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
            Blobs find = nowBranch.HEAD.getBlob();
            /** 存储路径 */
            String index = hashCode.substring(0 , 2);
            File whereAdding = join(GITLET_DIR , "stagingArea");
            /** 检查当前commit的文件 所add文件是否已存在当前commit*/
            if(find != null && find.searchExist(name) && (Blobs.getContents(find.search(name)).equals(contents))){
                    if (join(join(whereAdding, index), hashCode.substring(2)).exists())
                        /** 存在就删掉 */
                        join(join(whereAdding , index), hashCode.substring(2)).delete();
                        join(whereAdding , index).delete();
                System.exit(0);
            }
            /** 更新存储区文件内容 */
            Stage nowStage = readObject(join(Repository.GITLET_DIR , "StageFile") , Stage.class );
            if(nowStage.isExist(name)){
               nowStage.remove(name);
            }
            nowStage.add(hashCode , name);
            writeObject(join(Repository.GITLET_DIR, "StageFile") , nowStage);

            /** add File 到存储区*/
            join(whereAdding , index).mkdir();
            try {
                if (!join(join(whereAdding, index), hashCode.substring(2)).exists())
                join(join(whereAdding, index), hashCode.substring(2)).createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(join(join(whereAdding, index), hashCode.substring(2)) , contents);

    }

    /**向Stage区储存remove信息 并删除文件*/
    public static void addRemove(String name){
        File whereAdding = join(GITLET_DIR , "stagingArea");
        join(CWD , name).delete();
        if(!join(whereAdding , "00").exists()){
            join(whereAdding , "00").mkdir();
        }
        try {
            if (!join(join(whereAdding , "00") , "00000000000000000000000000000000000000").exists())
                join(join(whereAdding , "00") , "00000000000000000000000000000000000000").createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(join(whereAdding , "00") , "00000000000000000000000000000000000000") , readContentsAsString(join(join(whereAdding , "00") , "00000000000000000000000000000000000000")) + name + "\n");
    }

    /** 存储一个Branch */
    public static void addBranch(Branch root){
        String hashCode = root.code;
        File whereAdding = join(GITLET_DIR , "branch");
        String index = root.code.substring(0 , 2);
        if(!join(whereAdding , index).exists()) join(whereAdding , index).mkdir();
        try {
            if (!join(join(whereAdding, index), hashCode.substring(2)).exists())
                join(join(whereAdding, index), hashCode.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(join(join(whereAdding, index), hashCode.substring(2)) , root);
    }

    public static void savingBlobCWD(Blobs.Blob x){
        try {
            if (!join(CWD , x.getName()).exists())
                join(CWD , x.getName()).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(CWD , x.getName()) , Blobs.getContents(x));
    }


}
