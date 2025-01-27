package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.join;
import static gitlet.Utils.writeContents;

public class Blobs implements Serializable {
    private Blob root;


    public void savingBlob(Blob bro){
        String code = bro.hashCode;
        String index = code.substring(0 , 2);
        File whereSaving = join(Repository.GITLET_DIR , "objects");
        join(whereSaving , index).mkdir();
        try {
            if (!join(join(whereSaving, index), code.substring(2)).exists())
                join(join(whereSaving, index), code.substring(2)).createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeContents(join(join(whereSaving, index), code.substring(2)) , bro.contents);
    }

    public void checkBlobs(String code , String name , String contents){
        if(searchExist(name)){
            Blob bro = search(name);
            savingBlob(bro);
            bro.contents = contents;
            bro.hashCode = code;
            return;
        }else{
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
        if (this.root == null) root = new Blob(code, null, null, "BLACK" , name , contents);
        else this.root = addidk(this.root, code , name , contents);
        if (root != null) {
            root.color = "BLACK";
        }
    }

    private Blob addidk(Blob node, String code , String name , String contents) {
        if (node == null) {
            Blob sister = new Blob(code, null, null, "RED" , name , contents);
            savingBlob(sister);
            return sister;
        }
        int cmp = name.compareTo(node.name);
        if (cmp > 0) node.right = addidk(node.right, code , name , contents);
        else if (cmp < 0) node.left = addidk(node.left, code , name , contents);
        else return node;

        if (node.right.isRed() && !node.left.isRed()) {
            node = rotateLeft(node);
        } else if (node.left.isRed() && node.left.left.isRed()) {
            node = rotateRight(node);
        } else if (node.right.isRed() && node.left.isRed()) {
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


    public class Blob implements Serializable {
        private String contents;
        private String hashCode;
        private String name;
        private Blob left;
        private Blob right;
        private String color;

        public Blob(String code, Blob left, Blob right, String color , String name , String contents) {
            this.hashCode = code;
            this.left = left;
            this.right = right;
            this.color = color;
            this.name = name;
            this.contents = contents;
        }

        private boolean isRed() {
            return this.color.equals("RED");
        }

    }


}
