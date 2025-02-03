package gitlet;


import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date; // TODO: You'll likely use this in this class

import static gitlet.Main.ZERO;
import static gitlet.Repository.CWD;
import static gitlet.Utils.*;



public class Commit implements Serializable {

    public Metadata metadata;
    private final Commit parent;
    private final Commit parentMerge;
    private Blobs file;
    private final String hashCode;
    /**
     * The message of this Commit.
     */
    private String message;

    public Commit(Metadata data, Commit first, Commit second, Blobs filehere) {
        this.metadata = data;
        this.parent = first;
        this.parentMerge = second;
        this.file = filehere;
        this.hashCode = Utils.sha1(this.getBlob().toString() + this.metadata.logMessage + this.metadata.timestamp);
    }

    public void loadingCommit() {

        /** 获取hashcode 与存储路径*/
        String index = this.hashCode.substring(0, 2);
        File whereCommiting = join(Repository.GITLET_DIR, "commits");
        /** 创建相关文件夹 文件 */
        join(whereCommiting, index).mkdir();
        try {
            join(join(whereCommiting, index), this.hashCode.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /** 写入commit信息 */
        writeObject(join(join(whereCommiting, index), this.hashCode.substring(2)), this);

        /** 更改HEADBRANCH 文件信息*/
        Branch nowBranch = readObject(join(Repository.GITLET_DIR, Main.HEADBRANCH), Branch.class);
        nowBranch.HEAD = this;
        Repository.addBranch(nowBranch);
        writeObject(join(Repository.GITLET_DIR, Main.HEADBRANCH), nowBranch);
    }

    public void checkStage() {
        /** remake and add */
        Stage nowStage = readObject(join(Repository.GITLET_DIR, Main.STAGEFILE), Stage.class);
        this.file = nowStage.check(this.getBlob());
    }

    /**
     * 把所有这个commit跟踪的有关路径的文件删除
     */
    public void delete() {
        deleteTime(this.getBlob().getRoot());
    }

    private void deleteTime(Blobs.Blob x) {
        if (x == null) return;
        join(CWD, x.getName()).delete();
        deleteTime(x.getLeft());
        deleteTime(x.getRight());
    }

    /**
     * 检查一下这个commit要checkout过来的文件有没有被全部追踪
     */

    public void checkTrace() {
        Branch nowBranch = readObject(join(Repository.GITLET_DIR, Main.HEADBRANCH), Branch.class);
        checkTraceTime(this.getBlob().getRoot(), nowBranch);
    }

    private void checkTraceTime(Blobs.Blob x, Branch e) {
        if (x == null) return;
        /** checkout过来的Blobs的内容包含 now Blobs的内容 */
        File path = join(CWD , x.getName());
        if ( path.exists() && !e.HEAD.getBlob().searchExist(x.getName())) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            System.exit(0);
        }
        checkTraceTime(x.getLeft(), e);
        checkTraceTime(x.getRight(), e);
    }

    public void checkMerge(Commit point) {
        /**先遍历看看Splitpoint的*/
        System.out.println(point.getHashCode());
        point.file.printInOrder();
        System.out.println();

        System.out.println(this.getParent().getHashCode());
        this.getParent().file.printInOrder();
        System.out.println();

        System.out.println(this.getParentMerge().getHashCode());
        this.getParentMerge().file.printInOrder();
        System.out.println();
        this.checkMergeTime(point.file.getRoot(), this.getParentMerge());
        this.checkmainParent(this.getParent().file.getRoot(), this.getParentMerge(), point.file);
        this.checksecordParent(this.getParentMerge().file.getRoot(), this.getParentMerge(), point.file);
    }

    private void checkmainParent(Blobs.Blob x, Commit secordParent, Blobs point) {
        if (x == null) return;
        checkmainParent(x.getLeft(), secordParent, point);
        checkmainParent(x.getRight(), secordParent, point);
        Blobs.Blob secord = secordParent.file.search(x.getName());
        Blobs.Blob pointt = point.search(x.getName());

        /**仅在 当前存在的 不变 */
        if (pointt == null && secord == null) {

        }
        /** point没有 当前有 given也有 但相同 */
        else if (pointt == null && secord != null && secord.getHashCode().equals(x.getHashCode())) {

        }
        /** point没有 当前有 given也有 但不同 */
        else if (pointt == null && secord != null) {
            mergeContent(x, x, secord, secordParent);
        }
    }

    private void checksecordParent(Blobs.Blob x, Commit secordParent, Blobs point) {
        if (x == null) return;
        checkmainParent(x.getLeft(), secordParent, point);
        checkmainParent(x.getRight(), secordParent, point);
        /**仅在 given存在的 checkout */
        if (!point.searchExist(x.getName()) && !this.file.searchExist(x.getName())) {
            Repository.checkoutCommit(secordParent.getHashCode(), x.getName());
            this.file.add(x.getHashCode(), x.getName(), Blobs.getContents(x));
        }
    }

    private void checkMergeTime(Blobs.Blob x, Commit secordParent) {
        if (x == null) return;
        checkMergeTime(x.getLeft(), secordParent);
        checkMergeTime(x.getRight(), secordParent);
        /** 获取追踪文件 */
        Blobs.Blob main = this.file.search(x.getName());
        Blobs.Blob secord = secordParent.file.search(x.getName());
        /**有可能 分支有 后面两个没有吗？*/
        /** 任何存在于拆分点、当前分支中未修改且给定分支中不存在的文件都应被删除（并且未跟踪）。 */

        if(x.getHashCode().equals(ZERO)) return;
        if (main != null && secord == null && x.getHashCode().equals(main.getHashCode())) {
            Repository.addRemove(x.getName());
            this.file.removeBlob(x.getName());

        }
        /** 任何存在于拆分点、在给定分支中未修改且在当前分支中不存在的文件都应保持不存在。 */
        else if (main == null && secord != null && x.getHashCode().equals(secord.getHashCode())) {

        } else if (main == null && secord == null) {

        }
        /** 当前branch没变 given变了的Blob checkgiven */
        else if (x.getHashCode().equals(main.getHashCode()) && !x.getHashCode().equals(secord.getHashCode())) {

            Repository.checkoutCommit(secordParent.hashCode, x.getName());
            this.file.removeBlob(main.getName());
            this.file.add(secord.getHashCode(), secord.getName(), Blobs.getContents(secord));
        }
        /** 当前branch变了 given没变 check 不变 */
        else if (!x.getHashCode().equals(main.getHashCode()) && x.getHashCode().equals(secord.getHashCode())) {

        }
        /** 两个文件现在具有相同的内容或都已被删除  不变*/
        else if (secord.getHashCode().equals(main.getHashCode())) {

            Repository.checkoutCommit(this.hashCode, x.getName());
        }
        /** 两个文件不同内容 */
        else {

            mergeContent(x, main, secord, secordParent);
        }
    }

    private void mergeContent(Blobs.Blob x, Blobs.Blob main, Blobs.Blob secord, Commit secordParent) {
        System.out.println("Encountered a merge conflict.");
        if (main.getHashCode().equals(ZERO) || secord.getHashCode().equals(ZERO)) {
            this.file.removeBlob(x.getName());
            this.file.add(ZERO, x.getName(), "");
        } else {
            this.file.removeBlob(x.getName());
            String contents = "<<<<<<< HEAD" + "\n" + Blobs.getContents(main) + "\n" + "=======" + "\n" + Blobs.getContents(secord) + "\n" + ">>>>>>>";
            this.file.add(sha1(contents + x.getName()), x.getName(), contents);
            Repository.checkoutCommit(this.getHashCode(), x.getName());
        }
    }

    /**
     * 把所有这个commit跟踪的有关文件导入进来
     */
    public void get() {
        getTime(this.getBlob().getRoot());
    }

    private void getTime(Blobs.Blob x) {
        if (x == null) return;
        try {
            if (!join(CWD, x.getName()).exists()) {
                if (x.getHashCode().equals(ZERO)) {
                    return;
                }
                join(CWD, x.getName()).createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(CWD, x.getName()), Blobs.getContents(x));
        getTime(x.getLeft());
        getTime(x.getRight());
    }


    public Blobs getBlob() {
        return this.file;
    }

    public Commit getParent() {
        return this.parent;
    }

    public Commit getParentMerge() {
        return this.parentMerge;
    }

    public String getHashCode() {
        return this.hashCode;
    }


}
