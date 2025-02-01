package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static gitlet.Main.*;
import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 *  does at a high level.
 * <p>
 * kenqia
 */
public class Repository {

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");


    public static void init() {
        /**创建各种文件夹*/
        if (!GITLET_DIR.exists()) {
            GITLET_DIR.mkdir();
            join(GITLET_DIR, "commits").mkdir();
            join(GITLET_DIR, "stagingArea").mkdir();
            join(GITLET_DIR, "objects").mkdir();
            /**存储删除信息 */
            join(join(GITLET_DIR, "objects"), "00").mkdir();
            try {
                join(join(join(GITLET_DIR, "objects"), "00"), "00000000000000000000000000000000000000").createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            join(GITLET_DIR, "branch").mkdir();
        } else {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
    }

    public static void add(String name) {
        /** 获取要add的文件路径 */
        File theFile = join(CWD, name);
        /**文件不存在 检查是不是被删掉了 */
        Stage nowStage = readObject(join(Repository.GITLET_DIR, STAGEFILE), Stage.class);
        if (!theFile.exists()) {
            Stage.node x = nowStage.search(name);
            if (x != null && x.getCode().equals(ZERO)) {
                nowStage.remove(name);
                removeRemove(name);
                System.exit(0);
                writeObject(join(Repository.GITLET_DIR, STAGEFILE), nowStage);
            }
            else {
                System.out.println("File does not exist.");
                System.exit(0);
            }
        }
        /**获取文件 内容 hashcode */
        String contents = readContentsAsString(theFile);
        String hashCode = sha1(contents + name);
        /** 检查当前commit的文件 , 先获取其Blobs*/
        Branch nowBranch = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
        Blobs find = nowBranch.HEAD.getBlob();
        /** 存储路径 */
        String index = hashCode.substring(0, 2);
        File whereAdding = join(GITLET_DIR, "stagingArea");
        /** 检查当前commit的文件 所add文件是否已存在当前commit*/
        if(find != null) {
            Blobs.Blob item = find.search(name);
            if (item != null && item.getHashCode().equals(hashCode)) {
                if (join(join(whereAdding, index), hashCode.substring(2)).exists()) {
                    /** 存在就删掉 */
                    join(join(whereAdding, index), hashCode.substring(2)).delete();
                    join(whereAdding, index).delete();
                }
                System.exit(0);
            }
        }
        /** 更新存储区文件内容 */
        if (nowStage.isExist(name)) {
            nowStage.remove(name);
        }
        nowStage.add(hashCode, name);
        writeObject(join(Repository.GITLET_DIR, STAGEFILE), nowStage);

        /** add File 到存储区*/
        join(whereAdding, index).mkdir();
        try {
            if (!join(join(whereAdding, index), hashCode.substring(2)).exists())
                join(join(whereAdding, index), hashCode.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(join(whereAdding, index), hashCode.substring(2)), contents);

    }

    /**
     * 向Stage区储存remove信息 并删除文件
     */
    public static void addRemove(String name) {
        File whereAdding = join(GITLET_DIR, "stagingArea");
        join(CWD, name).delete();
        if (!join(whereAdding, "00").exists()) {
            join(whereAdding, "00").mkdir();
        }
        try {
            if (!join(join(whereAdding, "00"), "00000000000000000000000000000000000000").exists())
                join(join(whereAdding, "00"), "00000000000000000000000000000000000000").createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(join(whereAdding, "00"), "00000000000000000000000000000000000000"), readContentsAsString(join(join(whereAdding, "00"), "00000000000000000000000000000000000000")) + name + "\n");
    }
    /**
     * 向Stage区删除一个remove信息 并恢复文件
     */
    public static void removeRemove(String name){
        File whereAdding = join(GITLET_DIR, "stagingArea");
        Branch nowBranch = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
        /** 恢复文件 */
        checkoutCommit(nowBranch.HEAD.getHashCode() , name);
        writeContents(join(join(whereAdding, "00"), "00000000000000000000000000000000000000"), readContentsAsString(join(join(whereAdding, "00"), "00000000000000000000000000000000000000")) + name + "\n");
        String[] contents = readContentsAsString(join(join(whereAdding, "00"), "00000000000000000000000000000000000000")).split("\n");
        writeContents(join(join(whereAdding, "00"), "00000000000000000000000000000000000000"), "");
        for(String item : contents){
            if(item.equals(name)) continue;
            writeContents(join(join(whereAdding, "00"), "00000000000000000000000000000000000000"), readContentsAsString(join(join(whereAdding, "00"), "00000000000000000000000000000000000000")) + item + "\n");
        }
    }

    /**
     * 存储或重写一个Branch
     */
    public static void addBranch(Branch root) {
        String hashCode = root.code;
        File whereAdding = join(GITLET_DIR, "branch");
        String index = root.code.substring(0, 2);
        if (!join(whereAdding, index).exists()) join(whereAdding, index).mkdir();
        try {
            if (!join(join(whereAdding, index), hashCode.substring(2)).exists())
                join(join(whereAdding, index), hashCode.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(join(join(whereAdding, index), hashCode.substring(2)), root);
    }

    public static boolean searchBranchExist(String name) {
        File whereBranch = join(Repository.GITLET_DIR, "branch");
        String[] branchHere = whereBranch.list();
        for (String item : branchHere) {
            File branchNow = join(whereBranch, item);
            List<String> branch = plainFilenamesIn(branchNow);
            for (String one : branch) {
                Branch branchItem = readObject(join(branchNow, one), Branch.class);
                if (branchItem.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Branch searchBranch(String name) {
        File whereBranch = join(Repository.GITLET_DIR, "branch");
        String[] branchHere = whereBranch.list();
        for (String item : branchHere) {
            File branchNow = join(whereBranch, item);
            List<String> branch = plainFilenamesIn(branchNow);
            for (String one : branch) {
                Branch branchItem = readObject(join(branchNow, one), Branch.class);
                if (branchItem.name.equals(name)) {
                    return branchItem;
                }
            }
        }
        return null;
    }

    public static boolean searchCommitExist(String code) {
        File whereCommit = join(Repository.GITLET_DIR, "commits");
        String[] CommitHere = whereCommit.list();
        for (String item : CommitHere) {
            File commitNow = join(whereCommit, item);
            List<String> commit = plainFilenamesIn(commitNow);
            for (String one : commit) {
                Commit commitItem = readObject(join(commitNow, one), Commit.class);
                if (commitItem.getHashCode().equals(code)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Commit searchCommit(String code) {
        File whereCommit = join(Repository.GITLET_DIR, "commits");
        String[] CommitHere = whereCommit.list();
        for (String item : CommitHere) {
            File commitNow = join(whereCommit, item);
            List<String> commit = plainFilenamesIn(commitNow);
            for (String one : commit) {
                Commit commitItem = readObject(join(commitNow, one), Commit.class);
                if (commitItem.getHashCode().equals(code)) {
                    return commitItem;
                }
            }
        }
        return null;
    }

    public static void removeBranch(Branch x) {
        String hashCode = x.code;
        File whereRemove = join(GITLET_DIR, "branch");
        String index = x.code.substring(0, 2);
        join(join(whereRemove, index), hashCode.substring(2)).delete();
        join(whereRemove, index).delete();
    }

    public static void savingBlobCWD(Blobs.Blob x) {
        try {
            if (!join(CWD, x.getName()).exists()) join(CWD, x.getName()).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(CWD, x.getName()), Blobs.getContents(x));
    }

    public static void checkoutCommit(String ID, String name) {
        if (ID.equals(ZERO)) return;
        /** 遍历搜索  */
        Commit x = searchCommit(ID);
        if (x == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Blobs.Blob file = x.getBlob().search(name);
        /** 文件不存在此commit中 */
        if (file == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        /** 存在file 存储 */
        else {
            Repository.savingBlobCWD(file);
        }
    }


    public static void checkoutBranch(String name) {
        Branch nowBranch = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
        if (name.equals(nowBranch.name)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File whereBranch = join(Repository.GITLET_DIR, "branch");
        String[] branchHere = whereBranch.list();
        for (String item : branchHere) {
            int flag = 0;
            File branchNow = join(whereBranch, item);
            List<String> branch = plainFilenamesIn(branchNow);
            for (String one : branch) {
                Branch branchItem = readObject(join(branchNow, one), Branch.class);
                if (branchItem.name.equals(name)) {
                    branchItem.HEAD.checkTrace();
                    nowBranch.HEAD.delete();
                    branchItem.HEAD.get();
                    writeObject(join(Repository.GITLET_DIR, HEADBRANCH), branchItem);
                    flag = 1;
                    break;
                }
            }
            if (flag == 1) {
                /**  重置 stage */
                writeObject(join(Repository.GITLET_DIR, STAGEFILE), new Stage(10));
                System.exit(0);

            }
        }

        System.out.println("No such branch exists.");
        System.exit(0);
    }

    public static void checkoutBranch(Branch x) {
        Branch nowBranch = readObject(join(Repository.GITLET_DIR, HEADBRANCH), Branch.class);
        if (x.name.equals(nowBranch.name)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        x.HEAD.checkTrace();
        nowBranch.HEAD.delete();
        x.HEAD.get();
        writeObject(join(Repository.GITLET_DIR, HEADBRANCH), x);
        /**  重置 stage */
        writeObject(join(Repository.GITLET_DIR, STAGEFILE), new Stage(10));
    }
}
