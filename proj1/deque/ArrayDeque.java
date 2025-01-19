package deque;

import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayDeque<Item> implements List<Item> {
    Item[] items;
    int size = 0;

    public ArrayDeque() {
        this.items = (Item[])new Object[8];
        this.size = 8;
    }

    public void addFirst(Item L) {
        Item[] newOne = (Item[])new Object[size + 1];
        newOne[0] = L;
        System.arraycopy(items, 0, newOne, 1, size);
        size++;
        items = newOne;
    }

    public void addLast(Item L) {
        Item[] newOne = (Item[])new Object[size + 1];
        newOne[size] = L;
        System.arraycopy(items, 0, newOne, 0, size);
        size++;
        items = newOne;
    }

    public boolean isEmpty(){
        for (int i = 0 ; i < size ; i++){
            if(items[i] != null){
                return false;
            }
        }
        return true;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        for (int i = 0 ; i < size ; i++){
                System.out.print(items[i] );
            }
        System.out.println();
    }

    public Item removeFirst(){
        Item[] newOne = (Item[])new Object[size - 1];
        Item saving = items[0];
        System.arraycopy(items, 1, newOne, 0, size - 1);
        size--;
        items = newOne;
        return  saving;
    }

    public Item removeLast(){
        Item[] newOne = (Item[])new Object[size - 1];
        Item saving = items[size - 1];
        System.arraycopy(items, 0, newOne, 0, size - 1);
        size--;
        items = newOne;
        return  saving;
    }

    public Item get(int index){
        if (index>=size) return null;
        return items[index];
    }


    public Iterator<Item> interator(){
        return null;
    }

    public boolean equals(Object o){
        return true;
    }

}
