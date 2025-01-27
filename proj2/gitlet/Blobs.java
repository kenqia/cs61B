package gitlet;

import java.io.Serializable;

public class Blobs implements Serializable {
    private Blob root;

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

    public void add(String code) {
        if (this.root == null) root = new Blob(code, null, null, "BLACK");
        else this.root = addidk(this.root, code);
        if (root != null) {
            root.color = "BLACK";
        }
    }

    private Blob addidk(Blob node, String code) {
        if (node == null) {
            return new Blob(code, null, null, "RED");
        }
        int cmp = code.compareTo(node.hashCode);
        if (cmp > 0) node.right = addidk(node.right, code);
        else if (cmp < 0) node.left = addidk(node.left, code);
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
        String hashCode;
        Blob left;
        Blob right;
        String color;

        public Blob(String code, Blob left, Blob right, String color) {
            this.hashCode = code;
            this.left = left;
            this.right = right;
            this.color = color;
        }

        private boolean isRed() {
            return this.color.equals("RED");
        }

    }


}
