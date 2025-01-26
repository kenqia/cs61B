package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blobs implements Serializable {
    private Blob root;

    public Blob search(String name) {
        if (name == null) return null;
        else {
            return searchTime(root, name);
        }
    }

    private Blob searchTime(Blob root, String name) {
        if (root == null) return null;
        int cmp = name.compareTo(root.fileName);
        if (cmp > 0) return searchTime(root.right, name);
        else if (cmp < 0) return searchTime(root.left, name);
        else return root;
    }

    public void add(String name) {
        if (this.root == null) root = new Blob(name, null, null, "BLACK");
        else this.root = addidk(this.root, name);
        if (root != null) {
            root.color = "BLACK";
        }
    }

    private Blob addidk(Blob node, String name) {
        if (node == null) {
            return new Blob(name, null, null, "RED");
        }
        int cmp = name.compareTo(node.fileName);
        if (cmp > 0) node.right = addidk(node.right, name);
        else if (cmp < 0) node.left = addidk(node.left, name);
        else return null;

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
        String fileName;
        Blob left;
        Blob right;
        String color;

        public Blob(String name, Blob left, Blob right, String color) {
            this.fileName = name;
            this.left = left;
            this.right = right;
            this.color = color;
        }

        private boolean isRed() {
            return this.color.equals("RED");
        }

    }


}
