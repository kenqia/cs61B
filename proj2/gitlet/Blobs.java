package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;

/** Blobs 是存储Blob的LLRB树 , Blob 是存储文件信息的类 */
public class Blobs implements Serializable {
    private Blob root; /** Top Blob */

    public Blobs(Blob root){
        this.root = root;
    }

    public Blob getRoot() {
        return root;
    }

    /** 把指定Blob的contents(由hash code搜索 存储contents)存储起来 , 放到objects 区 */
    public void savingBlob(Blob bro , String contents){

        String code = bro.hashCode;
        /** 存储 */
        String index = code.substring(0 , 2);
        File whereSaving = join(Repository.GITLET_DIR , "objects");
        join(whereSaving , index).mkdir();
        try {
            if (!join(join(whereSaving, index), code.substring(2)).exists())
                join(join(whereSaving, index), code.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /** 写入 */
        writeContents(join(join(whereSaving, index), code.substring(2)) , contents);
    }

    /** commit时 检查stage区与上一个commit的关系 */
    public void checkBlobs(String code , String name , String contents){
        /** 若有相同名字 ， 则覆盖 */
        if(searchExist(name)){
            Blob bro = search(name);
            /** 判断是否为移除操作 */
            if(!code.equals("0000000000000000000000000000000000000000")){
                savingBlob(bro , contents);
            }
            bro.hashCode = code;
        }else{
            /** 没有 则添加 */
            add(code , name , contents);
        }
    }
    public Blob search(String name) {
        if (name == null) return null;
        else {
            return searchTime(root, name);
        }
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


    private Blob searchTime(Blob root, String name) {
        if (root == null) return null;
        int cmp = name.compareTo(root.name);
        if (cmp > 0) return searchTime(root.right, name);
        else if (cmp < 0) return searchTime(root.left, name);
        else return root;
    }

    public void add(String code , String name , String contents) {
        if (this.root == null){
            root = new Blob(code, null, null, "BLACK" , name);
            if(!(contents == null)) savingBlob(root , contents);
        }
        else this.root = addidk(this.root, code , name , contents);
        if (root != null) {
            root.color = "BLACK";
        }
    }

    private Blob addidk(Blob node, String code , String name , String contents) {
        if (node == null) {
            Blob sister = new Blob(code, null, null, "RED" , name);
            if(!(contents == null)) savingBlob(root , contents);
            return sister;
        }
        int cmp = name.compareTo(node.name);
        if (cmp > 0) node.right = addidk(node.right, code , name , contents);
        else if (cmp < 0) node.left = addidk(node.left, code , name , contents);
        else return node;

        if (isRed(node.right) && !isRed(node.left)) {
            node = rotateLeft(node);
        } else if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        } else if (isRed(node.right) && isRed(node.left)) {
            filpColor(node);
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

    private void filpColor(Blob node) {
        if (node.color.equals("RED")) node.color = "BLACK";
        else node.color = "RED";
        if (node.left != null) node.left.color = "BLACK";
        if (node.right != null) node.right.color = "BLACK";
    }

    private boolean isRed(Blob node){
        return node != null && node.color.equals("RED");
    }

    private void removeBlob(String name){
        if(name == null) return;
        this.root = removeBlobTime(root , name);
    }

    private Blob removeBlobTime(Blob root , String name){
        if(root == null ) return null;

        int cmp = name.compareTo(root.name);
        if(cmp > 0){
            root.right = removeBlobTime(root.right , name);
        }
        else if(cmp < 0){
            root.left = removeBlobTime(root.left , name);
        }
        else{
            if(root.left == null && root.right == null) return null;
            else if(root.left != null && root.right != null){
                root = swapAfter(root);
            }
            else{
                if(root.left != null) return root.left;
                return root.right;
            }
        }
        if (isRed(root.right) && !isRed(root.left)) {
            root = rotateLeft(root);
        } else if (isRed(root.left) && isRed(root.left.left)) {
            root = rotateRight(root);
        } else if (isRed(root.right) && isRed(root.left)) {
            filpColor(root);
        }
        return root;
        
    }
        /** 交换前驱节点并删除 */
    private Blob swapAfter(Blob root){
        if(root.left.right != null){
            root.hashCode = root.left.right.hashCode;
            root.name = root.left.right.name;
            if(root.left.right.left == null && root.left.right.right == null){
                root.left.right=null;
            }
            else{
                if(root.left.right.left != null) root.left.right = root.left.right.left;
                else root.left.right = root.left.right.right;
            }
            return root;
        }
        else{
            root.hashCode = root.left.hashCode;
            root.name = root.left.name;
            if(root.left.left == null){
                root.left=null;
            }
            else{
                root.left = root.left.left;
            }
            return root;
        }
    }

    public static String getContents(Blob x){
        String code = x.hashCode;
        if (code.equals("0000000000000000000000000000000000000000")) return null;
        /** 存储 */
        String index = code.substring(0 , 2);
        File whereSaving = join(Repository.GITLET_DIR , "objects");
        return readContentsAsString(join(join(whereSaving, index), code.substring(2)));
    }


    public class Blob implements Serializable {
        private String hashCode;
        private String name;
        private Blob left;
        private Blob right;
        private String color;

        public Blob(String code, Blob left, Blob right, String color , String name) {
            this.hashCode = code;
            this.left = left;
            this.right = right;
            this.color = color;
            this.name = name;
        }

        public String getName(){
            return this.name;
        }
        public Blob getLeft(){
            return this.left;
        }
        public Blob getRight(){
            return this.right;
        }

        public String getHashCode(){
            return this.hashCode;
        }

    }


}
