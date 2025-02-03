
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
        String code = x.hashCode;
        if (code.equals(ZERO)) return null;
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
            if(code.equals(ZERO)){
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
            this.root = new Blob(code, null, null, "BLACK", name);
            savingBlob(root, contents);
        } else this.root = addidk(this.root, code, name, contents);
        if (root != null) {
            root.color = "BLACK";
        }
    }

    private Blob addidk(Blob node, String code, String name, String contents) {
        if (node == null) {
            Blob sister = new Blob(code, null, null, "RED", name);
            savingBlob(sister, contents);
            return sister;
        }
        int cmp = name.compareTo(node.name);
        if (cmp > 0) node.right = addidk(node.right, code, name, contents);
        else if (cmp < 0) node.left = addidk(node.left, code, name, contents);
        else return node;

        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        } else if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        } else if (isRed(node.right) && isRed(node.left)) {
            node = filpColor(node);
        }
        return node;
    }

    private Blob rotateLeft(Blob node) {
        Blob x = node.right;
        node.right = x.left;
        x.left = node;
        x.color = node.color;
        node.color = "RED";
        return x;
    }

    private Blob rotateRight(Blob node) {
        Blob x = node.left;
        node.left = x.right;
        x.right = node;
        x.color = node.color;
        node.color = "RED";
        return x;
    }
    public void printInOrder(){
        printInOrderFinding(this.root);
    }

    private void printInOrderFinding(Blob x){
        if(x == null ) return;

        printInOrderFinding(x.left);
        System.out.println(x.name + ":" +  x.hashCode);
        printInOrderFinding(x.right);
    }

    private Blob filpColor(Blob node) {
        if (node.color.equals("RED")) node.color = "BLACK";
        else node.color = "RED";
        if (node.left != null) node.left.color = "BLACK";
        if (node.right != null) node.right.color = "BLACK";
        return node;
    }

    private void flipColor(Blob node) {
        if (node.color.equals("RED")) node.color = "BLACK";
        else node.color = "RED";
        if (node.left != null) node.left.color = "BLACK";
        if (node.right != null) node.right.color = "BLACK";
    }

    private boolean isRed(Blob node) {
        return node != null && node.color.equals("RED");
    }

    // Remove a Blob (maintaining LLRB property)
    public void removeBlob(String name) {
        if (name == null) return;
        root = removeBlobTime(root, name);
        if (root != null) root.color = "BLACK";
    }

    private Blob removeBlobTime(Blob node, String name) {
        if (node == null) return null;

        int cmp = name.compareTo(node.name);
        if (cmp < 0) {
            if (!isRed(node.left) && !isRed(node.left.left)) {
                node = moveRedLeft(node);
            }
            node.left = removeBlobTime(node.left, name);
        } else {
            if (isRed(node.left)) {
                node = rotateRight(node);
            }
            if (cmp == 0 && node.right == null) {
                return null;
            }
            if (!isRed(node.right) && !isRed(node.right.left)) {
                node = moveRedRight(node);
            }
            if (cmp == 0) {
                Blob min = getMin(node.right);
                node.name = min.name;
                node.hashCode = min.hashCode;
                node.right = removeMin(node.right);
            } else {
                node.right = removeBlobTime(node.right, name);
            }
        }

        return balance(node);
    }

    private Blob moveRedLeft(Blob node) {
        flipColor(node);
        if (isRed(node.right.left)) {
            node.right = rotateRight(node.right);
            node = rotateLeft(node);
            flipColor(node);
        }
        return node;
    }

    private Blob moveRedRight(Blob node) {
        flipColor(node);
        if (isRed(node.left.left)) {
            node = rotateRight(node);
            flipColor(node);
        }
        return node;
    }

    private Blob getMin(Blob node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Blob removeMin(Blob node) {
        if (node.left == null) return null;
        if (!isRed(node.left) && !isRed(node.left.left)) {
            node = moveRedLeft(node);
        }
        node.left = removeMin(node.left);
        return balance(node);
    }

    private Blob balance(Blob node) {
        if (isRed(node.right)) node = rotateLeft(node);
        if (isRed(node.left) && isRed(node.left.left)) node = rotateRight(node);
        if (isRed(node.left) && isRed(node.right)) flipColor(node);
        return node;
    }

    public class Blob implements Serializable {
        public Blob left;
        public Blob right;
        private String hashCode;
        private String name;
        private String color;

        public Blob(String code, Blob left, Blob right, String color, String name) {
            this.hashCode = code;
            this.left = left;
            this.right = right;
            this.color = color;
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