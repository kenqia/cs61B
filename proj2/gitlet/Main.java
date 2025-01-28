package gitlet;

import javax.imageio.IIOException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                    Commit init = new Commit(new Metadata("1970-01-01 00：00：00", args[1]), null, null, new Blobs());
                    init.loadingCommit();
                }else{
                    Commit init = new Commit(new Metadata("1970-01-01 00：00：00", null), null, null, new Blobs());
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
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                /** 查看HEADBRANCH 复制上一个commit 并且导入时间与message */
                Branch nowBranch = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                Commit wantToCommit = new Commit(new Metadata(formatter.format(date), args[1]), nowBranch.HEAD, null, nowBranch.HEAD.getBlob());

                /** 新Commit 添加stage区数据 */
                wantToCommit.checkStage();

                wantToCommit.loadingCommit();
                /**  重置 stage */
                writeObject(join(Repository.GITLET_DIR, "StageFile") , new Stage(10));
                break;
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
                }

            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}
