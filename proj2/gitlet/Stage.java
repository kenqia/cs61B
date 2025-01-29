package gitlet;

import javax.swing.*;
import java.io.File;
import java.io.Serializable;
import java.util.TreeSet;

import static gitlet.Repository.GITLET_DIR;
import static gitlet.Utils.*;
/** 存储存储区的数据 使用hash table */
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
        int index = Math.abs(code.hashCode()) % head.length;
        node adding = new node(null, code , name);
        addlast(head[index], adding);
        head[index].size++;
        if(this.head[index].size == 1) this.size++;
        check(this.size, head[index].size);
    }

    private void addlast(node head, node hehe) {
        while (head.next != null) {
            head = head.next;
        }
        head.next = hehe;
    }

    /** 检查 比例 调整大小 */
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
    /** commit操作中 遍历存储区 执行操作 */
    public Blobs check(Blobs Trees){
        for (int i = 0; i < this.head.length; i++) {
            node x = head[i].next;
            while (x != null) {
                String index = x.code.substring(0 , 2);
                File find = join(GITLET_DIR , "stagingArea");
                String contents = getContents(x);
                Trees.checkBlobs(x.code , x.name , contents);
                join(join(find, index), x.code.substring(2)).delete();
                join(find, index).delete();
                x = x.next;
            }
        }
        return Trees;
    }

    public boolean isExist(String name){
        for (int i = 0; i < this.head.length; i++) {
            node x = head[i].next;
            while (x != null) {
                if(x.name.equals(name)) return true;
                x = x.next;
            }
        }
        return false;
    }

    public node remove(String name){
        for (int i = 0; i < this.head.length; i++) {
            node ptr = head[i];
            node x = head[i].next;
            while (x != null) {
                if(x.name.equals(name)){
                    ptr.next = x.next;
                    this.head[i].size--;
                    if(this.head[i].size == 0) this.size--;
                    return x;
                };
                x = x.next;
                ptr = ptr.next;
            }
        }
        return null;
    }

    public node removeStage(String name){
        for (int i = 0; i < this.head.length; i++) {
            node ptr = head[i];
            node x = head[i].next;
            while (x != null) {
                if(x.name.equals(name)){
                    ptr.next = x.next;
                    String index = x.code.substring(0 , 2);
                    File find = join(GITLET_DIR , "stagingArea");
                    join(join(find, index), x.code.substring(2)).delete();
                    join(find, index).delete();
                    this.head[i].size--;
                    if(this.head[i].size == 0) this.size--;
                    return x;
                };
                x = x.next;
                ptr = ptr.next;
            }
        }
        return null;
    }

    public int getSize(){
        return this.size;
    }

    public static String getContents(node x){
        String code = x.code;
        /** 存储 */
        String index = code.substring(0 , 2);
        File whereAdding = join(GITLET_DIR , "stagingArea");
        return readContentsAsString(join(join(whereAdding, index), code.substring(2)));
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
