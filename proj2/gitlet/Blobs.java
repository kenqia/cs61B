package gitlet;

import java.io.Serializable;

public class Blobs implements Serializable {
    private Blob root;


    public int checkBlobs(String code , String name){
        if(searchExist(code)){

        }
    }


    public Blob search(String code) {
        if (code == null) return null;
        else {
            return searchTime(root, code);
        }
    }

    public boolean searchExist(String code) {
        if (code == null) return false;
        else {
            return searchTimeExist(root, code);
        }
    }

    private boolean searchTimeExist(Blob root, String code) {
        if (root == null) return false;
        int cmp = code.compareTo(root.hashCode);
        if (cmp > 0) return searchTimeExist(root.right, code);
        else if (cmp < 0) return searchTimeExist(root.left, code);
        else return true;
    }


    private Blob searchTime(Blob root, String code) {
        if (root == null) return null;
        int cmp = code.compareTo(root.hashCode);
        if (cmp > 0) return searchTime(root.right, code);
        else if (cmp < 0) return searchTime(root.left, code);
        else return root;
    }

    public void add(String code , String name) {
        if (this.root == null) root = new Blob(code, null, null, "BLACK" , name);
        else this.root = addidk(this.root, code , name);
        if (root != null) {
            root.color = "BLACK";
        }
    }

    private Blob addidk(Blob node, String code , String name) {
        if (node == null) {
            return new Blob(code, null, null, "RED" , name);
        }
        int cmp = code.compareTo(node.hashCode);
        if (cmp > 0) node.right = addidk(node.right, code , name);
        else if (cmp < 0) node.left = addidk(node.left, code , name);
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

        private boolean isRed() {
            return this.color.equals("RED");
        }

    }


}
