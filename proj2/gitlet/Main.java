package gitlet;

import javax.imageio.IIOException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static gitlet.Utils.*;
import static java.util.Collections.sort;

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
                    /** arg[1] 是相对位置 */
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
                Branch nowBranchAnother = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                Blobs e = new Blobs(nowBranchAnother.HEAD.getBlob().getRoot());
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
                        if(commitItem.getParentMerge() != null) System.out.println("Merge: " + commitItem.getParent().getHashCode().substring(0 , 7) + " " + commitItem.getParentMerge().getHashCode().substring(0 , 7));
                        System.out.println("Date: " + commitItem.metadata.timestamp);
                        System.out.println(commitItem.metadata.logMessage);
                        System.out.println();
                    }
                }
                break;
                /** 凭借message 查询commit ID */
            case "find":
                int flag = 0;
                if(args.length == 1) {
                   System.exit(0);
                }
                File whereCommit1 = join(Repository.GITLET_DIR , "commits");
                String[] commitHere1 = whereCommit1.list();
                for (String item : commitHere1) {
                    File commitNow = join(whereCommit1 , item);
                    List<String> commit = plainFilenamesIn(commitNow);
                    for(String one : commit){
                        Commit commitItem = readObject(join(commitNow , one ) , Commit.class);
                        if(commitItem.metadata.logMessage == args[1]){
                            flag = 1;
                            System.out.println("commit " + commitItem.getHashCode());
                        }
                    }
                }
                if(flag == 0){
                    System.out.println("Found no commit with that message.");
                }
                break;
            /** 状态 */
            case "status":

                /** 遍历branch目录获取信息 打印branch信息 */
                System.out.println("=== Branches ===");
                Branch nowBranch3 = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                System.out.println("*" + nowBranch3.name);
                File whereBranch = join(Repository.GITLET_DIR , "branch");
                String[] branchHere = whereBranch.list();
                List<String> Branches = new ArrayList<>();
                for (String item : branchHere) {
                    File branchNow = join(whereBranch , item);
                    List<String> branch = plainFilenamesIn(branchNow);
                    for(String one : branch){
                        Branch branchItem = readObject(join(branchNow , one) , Branch.class);
                        if(!branchItem.name.equals(nowBranch3.name))
                        Branches.add(branchItem.name);
                    }
                }
                sort(Branches);
                for(String item : Branches){
                    System.out.println(item);
                }
                System.out.println();

                /** 遍历stage文件 打印已stage文件信息  remove信息也在里面了*/
                Stage nowStage2 = readObject(join(Repository.GITLET_DIR , "stageFile") , Stage.class);
                nowStage2.printInfo();
                /** 另外两个状态 先留着*/
                System.out.println("=== Modifications Not Staged For Commit ===");
                System.out.println();
                System.out.println("=== Untracked Files ===");
                System.out.println();
                break;
            /** 转入到 */
            case "checkout":
                Branch nowBranch4 = readObject(join(Repository.GITLET_DIR , "HeadBranch") , Branch.class );
                if(args.length == 1) System.exit(0);
                /** 第一种java gitlet.Main checkout -- [file name] */
                else if (args[1].equals("--")){
                    Blobs head = nowBranch4.HEAD.getBlob();
                    /** 文件不存在此commit中 */
                    if(!head.searchExist(args[2])){
                        System.out.println("File does not exist in that commit.");
                        System.exit(0);
                    }
                    /** 存在file 存储到CWD */
                    else{
                        Blobs.Blob file = head.search(args[2]);
                        Repository.savingBlobCWD(file);
                    }
                }
                /** java gitlet.Main checkout [commit id] -- [file name] */
                else if(args[2].equals("--")){
                    /** 遍历搜索  */
                    Commit search = nowBranch4.HEAD;
                    while(search  != null){
                        if(search.getHashCode().equals(args[1])){
                            /** 文件不存在此commit中 */
                            if(!search.getBlob().searchExist(args[3])){
                                System.out.println("File does not exist in that commit.");
                                System.exit(0);
                            }
                            /** 存在file 存储 */
                            else{
                                Blobs.Blob file = search.getBlob().search(args[3]);
                                Repository.savingBlobCWD(file);
                                break;
                            }
                        }
                        search = search.getParent();
                    }
                    if(search == null){
                        System.out.println("No commit with that id exists.");
                    }
                }
                /** java gitlet.Main checkout [branch name] */
                else{
                    if(args[1].equals(nowBranch4.name)){
                        System.out.println("No need to checkout the current branch.");
                        System.exit(0);
                    }
                    File whereBranch1 = join(Repository.GITLET_DIR , "branch");
                    String[] branchHere1 = whereBranch1.list();
                    List<String> Branches1 = new ArrayList<>();
                    for (String item : branchHere1) {
                        int flag1 = 0;
                        File branchNow = join(whereBranch1 , item);
                        List<String> branch = plainFilenamesIn(branchNow);
                        for(String one : branch){
                            Branch branchItem = readObject(join(branchNow , one) , Branch.class);
                            if(branchItem.name.equals(args[1])){
                                branchItem.HEAD.checkTrace();
                                nowBranch4.HEAD.delete();
                                branchItem.HEAD.get();
                                writeObject(join(Repository.GITLET_DIR, "HeadBranch") , branchItem);
                                flag1 = 1;
                                break;
                            }
                        }
                        if(flag1 == 1){
                            /**  重置 stage */
                            writeObject(join(Repository.GITLET_DIR, "StageFile") , new Stage(10));
                            System.exit(0);

                        }
                    }

                    System.out.println("No such branch exists.");
                    System.exit(0);
                }
                break;
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}
