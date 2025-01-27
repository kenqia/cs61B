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
        if(!GITLET_DIR.exists()){
            GITLET_DIR.mkdir();
            join(GITLET_DIR , "commits").mkdir();
            join(GITLET_DIR , "stagingArea").mkdir();
            join(GITLET_DIR , "objects").mkdir();
        }else{
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void add(String name){
        File theFile = join(join(CWD , "gitlet") , name);
        if(!theFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }else{
            String contents = readContentsAsString(theFile);
            String hashCode = sha1(readContents(theFile));

            Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
            Blobs find = nowBranch.HEAD.getBlob();

            String index = hashCode.substring(0 , 2);
            File whereAdding = join(GITLET_DIR , "stagingArea");

            if(find != null && find.searchExist(hashCode)){
                    if (join(join(whereAdding, index), hashCode.substring(2)).exists())
                        restrictedDelete( join(join(whereAdding, index), hashCode.substring(2)));
                System.exit(0);
            }

            join(whereAdding , index).mkdir();
            try {
                if (!join(join(whereAdding, index), hashCode.substring(2)).exists())
                join(join(whereAdding, index), hashCode.substring(2)).createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            writeContents(join(join(whereAdding, index), hashCode.substring(2)) , contents);
            Stage nowStage = readObject(join(Repository.GITLET_DIR , "StageFile") , Stage.class );
            nowStage.add(hashCode , name);
            writeObject(join(Repository.GITLET_DIR, "StageFile") , nowStage);
        }
    }


    public static void commit(){

    }


}
