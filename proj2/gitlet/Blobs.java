package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Main.ZERO;
import static gitlet.Utils.*;

/**
 * Blobs 是存储Blob的LLRB树 , Blob 是存储文件信息的类
 */
public class Blobs implements Serializable {
    public Blob root;

    /**
     * Top Blob
     */

    public Blobs(Blob root) {
        this.root = root;
    }


    public static String getContents(Blob x) {
        if (x == null) return "";
        String code = x.hashCode;
        /** 存储 */
        String index = code.substring(0, 2);
        File whereSaving = join(Repository.GITLET_DIR, "objects");
        return readContentsAsString(join(join(whereSaving, index), code.substring(2)));
    }

    public Blob getRoot() {
        return root;
    }

    /**
     * 把指定Blob的contents(由hash code搜索 存储contents)存储起来 , 放到objects 区
     */
    public void savingBlob(Blob bro, String contents) {
        /** 判断是否为移除操作 */
        if (bro.hashCode.equals(ZERO)) return;
        String code = bro.hashCode;
        /** 存储 */
        String index = code.substring(0, 2);
        File whereSaving = join(Repository.GITLET_DIR, "objects");
        join(whereSaving, index).mkdirs();
        try {
            if (!join(join(whereSaving, index), code.substring(2)).exists())
                join(join(whereSaving, index), code.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /** 写入 */
        writeContents(join(join(whereSaving, index), code.substring(2)), contents);
    }

    /**
     * commit时 检查stage区与上一个commit的关系
     */
    public void checkBlobs(String code, String name, String contents) {
        Blob bro = search(name);
        /** 若有相同名字 ， 则覆盖 */
        if (bro != null) {
            if (code.equals(ZERO)) {
                removeBlob(bro.getName());
                return;
            }
            removeBlob(name);
            add(code, name, contents);
        } else {
            /** 没有 则添加 */
            add(code, name, contents);
        }
    }

    public void printInOrder() {
        printInOrderFinding(this.root);
    }

    private void printInOrderFinding(Blob x) {
        if (x == null) return;

        printInOrderFinding(x.left);
        System.out.println(x.name + ":" + x.hashCode);
        printInOrderFinding(x.right);
    }

    public Blob search(String name) {
        if (name == null) return null;
        else {
            return searchTime(root, name);
        }
    }

    private Blob searchTime(Blob root, String name) {
        if (root == null) return null;
        int cmp = name.compareTo(root.name);
        if (cmp > 0) return searchTime(root.right, name);
        else if (cmp < 0) return searchTime(root.left, name);
        else return root;
    }

    public boolean searchExist(String name) {
        if (name == null) return false;
        else {
            return searchTimeExist(root, name);
        }
    }

    private boolean searchTimeExist(Blob root, String name) {
        if (root == null) return false;
        int cmp = name.compareTo(root.name);
        if (cmp > 0) return searchTimeExist(root.right, name);
        else if (cmp < 0) return searchTimeExist(root.left, name);
        else return true;
    }

    public void add(String code, String name, String contents) {
        if (this.root == null) {
            this.root = new Blob(code, null, null, name);
            savingBlob(root, contents);
        } else this.root = addidk(this.root, code, name, contents);
        if (root != null) {
        }
    }

    private Blob addidk(Blob node, String code, String name, String contents) {
        if (node == null) {
            Blob sister = new Blob(code, null, null, name);
            savingBlob(sister, contents);
            return sister;
        }
        int cmp = name.compareTo(node.name);
        if (cmp > 0) node.right = addidk(node.right, code, name, contents);
        else if (cmp < 0) node.left = addidk(node.left, code, name, contents);
        else return node;

        return node;
    }

    public void removeBlob(String name) {
        if (root == null) {
        }
        else {
            root = removeBlobTime(this.root, name);
        }
    }

    private Blob removeBlobTime(Blob x, String name) {
        if (x == null) return null;

        int cmp = name.compareTo(x.name);
        if (cmp > 0) x.right = removeBlobTime(x.right, name);
        else if (cmp < 0) x.left = removeBlobTime(x.left, name);
        else {
            if (x.left == null && x.right == null) {
                return null;
            } else if (x.left != null && x.right != null) {
                x = deleteTwochildren(x);
            } else {
                x = deleteOneChildren(x);
            }
        }
        return x;
    }


    private Blob deleteTwochildren(Blob x) {
        if (x.left.right != null) {
            swapBlob(x, x.left.right);
            if (x.left.right.left != null && x.left.right.right != null)
                x.left.right = deleteTwochildren(x.left.right);
            else if (x.left.right.left == null && x.left.right.right == null) x.left.right = null;
            else {
                x.left.right = deleteOneChildren(x.left.right);
            }
        }else {
            swapBlob(x, x.left);
            if(x.left.left != null) x.left = deleteOneChildren(x.left);
            else x.left = null;
        }
        return x;
    }

    private Blob deleteOneChildren(Blob x) {
        if (x.left != null) return x.left;
        return x.right;
    }

    private void swapBlob(Blob m, Blob n) {
        String temp = m.hashCode;
        m.hashCode = n.hashCode;
        n.hashCode = temp;

        temp = m.name;
        m.name = n.name;
        n.name = temp;
    }

    public class Blob implements Serializable {
        public Blob left;
        public Blob right;
        private String hashCode;
        private String name;

        public Blob(String code, Blob left, Blob right, String name) {
            this.hashCode = code;
            this.left = left;
            this.right = right;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public Blob getLeft() {
            return this.left;
        }

        public Blob getRight() {
            return this.right;
        }

        public String getHashCode() {
            return this.hashCode;
        }

        /**
         * 不包含路径的名字
         */
        public String getTruename() {
            return this.name.substring(this.name.lastIndexOf("/") + 1);
        }

    }

}
