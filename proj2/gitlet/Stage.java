package gitlet;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.TreeSet;

import static gitlet.Utils.*;

public class Stage implements Serializable {
    private node[] head;
    private int size;

    public Stage(int sizee) {
        head = new node[sizee];
        size = 0;
        for (int i = 0; i < head.length; i++) {
            head[i] = new node(null, "0" , "0");
            head[i].size = 0;
        }
    }

    public void add(String code , String name) {
        int index = Integer.parseInt(code) % head.length;
        node adding = new node(null, code , name);
        addlast(head[index], adding);
        size++;
        head[index].size++;
        check(this.size, head[index].size);
    }

    private void addlast(node head, node hehe) {
        while (head.next != null) {
            head = head.next;
        }
        head.next = hehe;
    }

    private void check(int stageSize, int dequeSize) {
        if ((double) dequeSize / (double) stageSize >= 1.5) {
            resize();
        }
    }

    private void resize() {
        Stage one = new Stage(2 * size);
        one.size = 0;
        for (int i = 0; i < this.head.length; i++) {
            node x = head[i].next;
            while (x != null) {
                one.add(x.code , x.name);
                x = x.next;
            }
        }
        this.head = one.head;
        this.size = one.size;
    }
    public node[] getHead(){
        return this.head;
    }

    public Blobs check(Blobs Trees){
        for (int i = 0; i < this.head.length; i++) {
            node x = head[i].next;
            while (x != null) {
                String index = x.code.substring(0 , 2);
                File find = join(Repository.GITLET_DIR , "stagingArea");
                String contents = readContentsAsString(join(join(find, index), x.code.substring(2)));
                Trees.checkBlobs(x.code , x.name , contents);
                x = x.next;
            }
        }
        return Trees;
    }


    private class node implements Serializable {
        private node next;
        private String code;
        private String name;
        private int size;

        private node(node next, String code , String name) {
            this.code = code;
            this.name = name;
            this.next = next;
        }


    }


}
