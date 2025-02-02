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

    static final String STAGEFILE = "StageFile";
    static final String HEADBRANCH = "HeadBranch";
    static final String ZERO = "0000000000000000000000000000000000000000";

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     */
    public static void main(String[] args) {
        /** 没参数时 */
        if (args.length == 0) {
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
                    join(Repository.GITLET_DIR, HEADBRANCH).createNewFile(); /**存储 头Branch是谁*/
                    join(Repository.GITLET_DIR, STAGEFILE).createNewFile();  /**存储 ？？*/
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                /** 新建存储文件信息 */
                writeObject(join(Repository.GITLET_DIR, HEADBRANCH), new Branch("master"));
                writeObject(join(Repository.GITLET_DIR, STAGEFILE), new Stage(10));
                /** 为init创建commit */
                if (args.length != 1) {
                    Commit init = new Commit(new Metadata("Wed Dec 31 16:00:00 1969 -0800", args[1]), null, null, new Blobs(null));
                    init.loadingCommit();
                } else {
                    Commit init = new Commit(new Metadata("Wed Dec 31 16:00:00 1969 -0800", "initial commit"), null, null, new Blobs(null));
                    init.loadingCommit();
                }
                break;
            /** add */
            case "add":

                /** 11221233333*/
                if (args.length == 1) {
                    System.exit(0);
                }
                /** 检查是否init */
                if (!Repository.GITLET_DIR.exists()) System.exit(0);
                /** arg[1] 是相对位置 */
                Repository.add(args[1]);

                break;
            case "commit":

                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                if (args[1].equals("")) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }

                /** 检查是否init */
                if (!Repository.GITLET_DIR.exists()) System.exit(0);
                /** 检查是否add */
                Stage nowStage = readObject(join(Repository.GITLET_DIR, STAGEFILE), Stage.class);
                if (nowStage.getSize() == 0) {
                    System.out.println("No changes added to the commit.");
                    System.exit(0);
                }

                /** 获取 时间数据 */
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);

                /** 查看HEADBRANCH 复制上一个commit 并且导入时间与message */
                Branch nowBranch = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                Blobs e = new Blobs(nowBranch.HEAD.getBlob().getRoot());
                Commit wantToCommit = new Commit(new Metadata(formatter.format(date), args[1]), nowBranch.HEAD, null, e);

                /** 新Commit 添加stage区数据 */
                wantToCommit.checkStage();

                wantToCommit.loadingCommit();
                /**  重置 stage */
                writeObject(join(Repository.GITLET_DIR, STAGEFILE), new Stage(10));
                break;
            /** 删除 */
            case "rm":
                if (args.length == 1) {
                    System.exit(0);
                }
                /** 检查是否init */
                if (!Repository.GITLET_DIR.exists()) System.exit(0);

                /** 取消暂存文件 */
                Stage nowStage1 = readObject(join(Repository.GITLET_DIR, STAGEFILE), Stage.class);
                if (nowStage1.isExist(args[1])) {
                    nowStage1.removeStage(args[1]);
                    writeObject(join(Repository.GITLET_DIR, STAGEFILE), nowStage1);
                    System.exit(0);
                }
                /** 添加删除信息 */
                Branch nowBranch1 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                if (nowBranch1.HEAD.getBlob().searchExist(args[1])) {
                    Repository.addRemove(args[1]);
                    nowStage1.add(ZERO, args[1]);
                    writeObject(join(Repository.GITLET_DIR, STAGEFILE), nowStage1);
                } else {
                    System.out.println("No reason to remove the file.");
                    System.exit(0);
                }
                break;
            /** 日志 */
            case "log":
                if (!Repository.GITLET_DIR.exists()) System.exit(0);
                Branch nowBranch2 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                Commit x = nowBranch2.HEAD;
                /**递归遍历commit内容 */
                while (x != null) {
                    System.out.println("===");
                    System.out.println("commit " + x.getHashCode());
                    if (x.getParentMerge() != null)
                        System.out.println("Merge: " + x.getParent().getHashCode().substring(0, 7) + " " + x.getParentMerge().getHashCode().substring(0, 7));
                    System.out.println("Date: " + x.metadata.timestamp);
                    System.out.println(x.metadata.logMessage);
                    System.out.println();
                    x = x.getParent();
                }
                break;
            /** 全局日志 */
            case "global-log":
                File whereCommit = join(Repository.GITLET_DIR, "commits");
                String[] commitHere = whereCommit.list();
                for (String item : commitHere) {
                    File commitNow = join(whereCommit, item);
                    List<String> commit = plainFilenamesIn(commitNow);
                    for (String one : commit) {
                        Commit commitItem = readObject(join(commitNow, one), Commit.class);
                        System.out.println("===");
                        System.out.println("commit " + commitItem.getHashCode());
                        if (commitItem.getParentMerge() != null)
                            System.out.println("Merge: " + commitItem.getParent().getHashCode().substring(0, 7) + " " + commitItem.getParentMerge().getHashCode().substring(0, 7));
                        System.out.println("Date: " + commitItem.metadata.timestamp);
                        System.out.println(commitItem.metadata.logMessage);
                        System.out.println();
                    }
                }
                break;
            /** 凭借message 查询commit ID */
            case "find":
                int flag = 0;
                if (args.length == 1) {
                    System.exit(0);
                }
                File whereCommit1 = join(Repository.GITLET_DIR, "commits");
                String[] commitHere1 = whereCommit1.list();
                for (String item : commitHere1) {
                    File commitNow = join(whereCommit1, item);
                    List<String> commit = plainFilenamesIn(commitNow);
                    for (String one : commit) {
                        Commit commitItem = readObject(join(commitNow, one), Commit.class);
                        if (commitItem.metadata.logMessage.equals(args[1])) {
                            flag = 1;
                            System.out.println(commitItem.getHashCode());
                        }
                    }
                }
                if (flag == 0) {
                    System.out.println("Found no commit with that message.");
                }
                break;
            /** 状态 */
            case "status":

                /** 遍历branch目录获取信息 打印branch信息 */
                System.out.println("=== Branches ===");
                Branch nowBranch3 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                File whereBranch = join(Repository.GITLET_DIR, "branch");
                String[] branchHere = whereBranch.list();
                List<String> Branches = new ArrayList<>();
                for (String item : branchHere) {
                    File branchNow = join(whereBranch, item);
                    List<String> branch = plainFilenamesIn(branchNow);
                    for (String one : branch) {
                        Branch branchItem = readObject(join(branchNow, one), Branch.class);
                        Branches.add(branchItem.name);
                    }
                }
                sort(Branches);
                for (String item : Branches) {
                    if (item.equals(nowBranch3.name)) System.out.print("*");
                    System.out.println(item);
                }
                System.out.println();

                /** 遍历stage文件 打印已stage文件信息  remove信息也在里面了*/
                Stage nowStage2 = readObject(join(Repository.GITLET_DIR, STAGEFILE), Stage.class);
                nowStage2.printInfo();
                /** 另外两个状态 先留着*/
                System.out.println("=== Modifications Not Staged For Commit ===");
                System.out.println();
                System.out.println("=== Untracked Files ===");
                System.out.println();
                break;
            /** 转入到 */
            case "checkout":
                Branch nowBranch4 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                if (args.length == 1) System.exit(0);

                /** java gitlet.Main checkout [branch name] */
                else if(args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                }
                /** java gitlet.Main checkout -- [file name] */
                else if (args.length == 3 &&args[1].equals("--")) {
                    Blobs head = nowBranch4.HEAD.getBlob();
                    Blobs.Blob file = head.search(args[2]);
                    /** 文件不存在此commit中 */
                    if (file == null) {
                        System.out.println("File does not exist in that commit.");
                        System.exit(0);
                    }
                    /** 存在file 存储到CWD */
                    else {
                        Repository.savingBlobCWD(file);
                    }
                }

                /** java gitlet.Main checkout [commit id] -- [file name] */
                else if (args.length == 4 && ( args[2].equals("--") || args[2].equals("++"))) {
                    Repository.checkoutCommit(args[1], args[3]);
                }
                break;
            case "branch":
                if (args.length != 2) {
                    System.exit(0);
                }
                if (Repository.searchBranchExist(args[1])) {
                    System.out.println("A branch with that name already exists.");
                    System.exit(0);
                }
                Branch nowBranch5 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                Branch newBranch = new Branch(args[1], nowBranch5.HEAD);
                newBranch.addMergeInfo(nowBranch5);
                Repository.addBranch(newBranch);
                nowBranch5.addMergeInfo(newBranch);
                Repository.addBranch(nowBranch5);
                break;
            case "rm-branch":
                if (args.length != 2) {
                    System.exit(0);
                }
                Branch nowBranch6 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                if (args[1].equals(nowBranch6.name)) {
                    System.out.println("Cannot remove the current branch.");
                }
                if (!Repository.searchBranchExist(args[1])) {
                    System.out.println("A branch with that name does not exist.");
                    System.exit(0);
                }
                Repository.removeBranch(Repository.searchBranch(args[1]));
                break;
            case "reset":
                Branch nowBranch7 = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
                if (args.length != 2) {
                    System.exit(0);
                }
                if (!Repository.searchCommitExist(args[1])) {
                    System.out.println("No commit with that id exists.");
                    System.exit(0);
                } else {
                    Commit w = Repository.searchCommit(args[1]);
                    w.checkTrace();
                    nowBranch7.HEAD.delete();
                    w.get();
                    nowBranch7.HEAD = w;
                    Repository.addBranch(nowBranch7);
                    writeObject(join(Repository.GITLET_DIR, HEADBRANCH), nowBranch7);
                    writeObject(join(Repository.GITLET_DIR, STAGEFILE), new Stage(10));
                    System.exit(0);
                }
                break;
            case "merge":
                /** 参数数量不对 */
                if (args.length != 2) {
                    System.exit(0);
                }
                Stage nowStage3 = readObject(join(Repository.GITLET_DIR, STAGEFILE), Stage.class);
                if (nowStage3.getSize() != 0) {
                    System.out.println("You have uncommitted changes.");
                    System.exit(0);
                }
                Branch nowBranch8 = readObject(join(Repository.GITLET_DIR, Main.HEADBRANCH), Branch.class);
                Branch theGivenBranch = Repository.searchBranch(args[1]);
                /** 不能merge自己 */
                if (args[1].equals(nowBranch8.name)) {
                    System.out.println("Cannot merge a branch with itself.");
                    System.exit(0);
                }
                /** 不存在这个Branch */
                if (!Repository.searchBranchExist(args[1])) {
                    System.out.println("A branch with that name does not exist.");
                    System.exit(0);
                }
                Branch.SplitPoint point = nowBranch8.getPoint(theGivenBranch);
                /** 如果 split point 与给定分支的提交相同 */
                if (theGivenBranch.HEAD.getHashCode().equals(point.getCommitCode())) {
                    System.out.println("Given branch is an ancestor of the current branch.");
                    System.exit(0);
                }
                /** if the split point is the current branch，那么效果是检出给定的分支 */
                if (nowBranch8.HEAD.getHashCode().equals(point.getCommitCode())) {
                    /** java gitlet.Main checkout [branch name] */
                    Repository.checkoutBranch(theGivenBranch);
                    System.out.println("Current branch fast-forwarded.");
                }
                /** 获取 时间数据 */
                Date dateNow = new Date();
                SimpleDateFormat formatterNow = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);

                /** 查看HEADBRANCH 复制上一个commit 并且导入时间与message */
                Blobs h = new Blobs(nowBranch8.HEAD.getBlob().getRoot());
                Commit mergeCommit = new Commit(new Metadata(formatterNow.format(dateNow), "1111"), nowBranch8.HEAD, theGivenBranch.HEAD, h);
                mergeCommit.checkMerge(point);
                System.out.println("Merged " + theGivenBranch.name + " into " + nowBranch8.name + ".");
                mergeCommit.loadingCommit();

                break;
            default:
                System.out.println("No command with that name exists.");
                break;

        }
    }
}
