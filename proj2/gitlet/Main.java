package gitlet;

import javax.imageio.IIOException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @kenqia
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        /** 没参数时 */
        if(args.length == 0){
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch (firstArg) {
            /** init 创建 */
            case "init":
                Repository.init();

                /** 创建信息储存文件*/
                try {
                    join(Repository.GITLET_DIR, "HeadBranch").createNewFile(); /**存储 头Branch是谁*/
                    join(Repository.GITLET_DIR, "StageFile").createNewFile();  /**存储 ？？*/
                }catch (IOException e) {
                    throw new RuntimeException(e);
                }

                /** 新建存储文件信息 */
                writeObject(join(Repository.GITLET_DIR, "HeadBranch") , new Branch("master"));
                writeObject(join(Repository.GITLET_DIR, "StageFile") , new Stage(10));
                /** 为init创建commit */
                if(args.length != 1) {
                    Commit init = new Commit(new Metadata("Wed Dec 31 16:00:00 1969 -0800", args[1]), null, null, new Blobs(null));
                    init.loadingCommit();
                }else{
                    Commit init = new Commit(new Metadata("Wed Dec 31 16:00:00 1969 -0800", "initial commit"), null, null, new Blobs(null));
                    init.loadingCommit();
                }
                break;
            /** add */
            case "add":

                /** 11221233333*/
                if(args.length == 1) {
                    System.exit(0);
                }
                /** 检查是否init */
                if(!Repository.GITLET_DIR.exists()) System.exit(0);

                Repository.add(args[1]);

                break;
            case "commit":
                if(args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                /** 检查是否init */
                if(!Repository.GITLET_DIR.exists()) System.exit(0);
                /** 检查是否add */
                Stage nowStage = readObject(join(Repository.GITLET_DIR , "StageFile") , Stage.class );
                if(nowStage.getSize() == 0){
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                }

                /** 获取 时间数据 */
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);

                /** 查看HEADBRANCH 复制上一个commit 并且导入时间与message */
                Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                Blobs e = new Blobs(nowBranch.HEAD.getBlob().getRoot());
                Commit wantToCommit = new Commit(new Metadata(formatter.format(date), args[1]), nowBranch.HEAD, null, e);

                /** 新Commit 添加stage区数据 */
                wantToCommit.checkStage();

                wantToCommit.loadingCommit();
                /**  重置 stage */
                writeObject(join(Repository.GITLET_DIR, "StageFile") , new Stage(10));
                break;
            /** 删除 */
            case "rm":
                if(args.length == 1) {
                    System.exit(0);
                }
                /** 检查是否init */
                if(!Repository.GITLET_DIR.exists()) System.exit(0);

                /** 取消暂存文件 */
                Stage nowStage1 = readObject(join(Repository.GITLET_DIR , "StageFile") , Stage.class );
                if(nowStage1.isExist(args[1])){
                    nowStage1.removeStage(args[1]);
                    writeObject(join(Repository.GITLET_DIR, "StageFile") , nowStage1);
                    System.exit(0);
                }
                /** 添加删除信息 */
                Branch nowBranch1 = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                if(nowBranch1.HEAD.getBlob().searchExist(args[1])){
                    Repository.addRemove(args[1]);
                    nowStage1.add("0000000000000000000000000000000000000000" , args[1] );
                    writeObject(join(Repository.GITLET_DIR, "StageFile") , nowStage1);
                }else{
                    System.out.println("No reason to remove the file.");
                    System.exit(0);
                }
                break;
            /** 日志 */
            case "log":
                if(!Repository.GITLET_DIR.exists()) System.exit(0);
                Branch nowBranch2 = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                Commit x = nowBranch2.HEAD;
                /**递归遍历commit内容 */
                while (x != null){
                    System.out.println("===");
                    System.out.println("commit " + x.getHashCode());
                    if(x.getParentMerge() != null) System.out.println("Merge: " + x.getParent().getHashCode().substring(0 , 7) + " " + x.getParentMerge().getHashCode().substring(0 , 7));
                    System.out.println("Date: " + x.metadata.timestamp);
                    System.out.println(x.metadata.logMessage);
                    System.out.println();
                    x = x.getParent();
                }

                break;
             /** 全局日志 */
            case "global-log":
                File whereCommit = join(Repository.GITLET_DIR , "commits");
                String[] commitHere = whereCommit.list();
                for (String item : commitHere) {
                    File commitNow = join(whereCommit , item);
                    List<String> commit = plainFilenamesIn(commitNow);
                    for(String one : commit){
                        Commit commitItem = readObject(join(commitNow , one ) , Commit.class);
                        System.out.println("===");
                        System.out.println("commit " + commitItem.getHashCode());
                        if(x.getParentMerge() != null) System.out.println("Merge: " + commitItem.getParent().getHashCode().substring(0 , 7) + " " + commitItem.getParentMerge().getHashCode().substring(0 , 7));
                        System.out.println("Date: " + commitItem.metadata.timestamp);
                        System.out.println(commitItem.metadata.logMessage);
                        System.out.println();
                    }
                }
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}
