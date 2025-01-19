package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements List<Item> {

    private int size = 0;
    private LinkedNode<Item> sentinel = new LinkedNode<Item>();

    public LinkedListDeque() {

        sentinel.front = sentinel;
        sentinel.next = sentinel;

    }

    public void addFirst(Item L) {
        LinkedNode<Item> newOne = new LinkedNode<Item>();
        newOne.L = L;
        newOne.next = sentinel.next;
        newOne.front = sentinel;
        sentinel.next.front = newOne;
        sentinel.next = newOne;
        size++;
    }

    public void addLast(Item L) {
        LinkedNode<Item> newOne = new LinkedNode<Item>();
        newOne.L = L;
        newOne.next = sentinel;
        newOne.front = sentinel.front;
        sentinel.front.next = newOne;
        sentinel.front = newOne;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        LinkedNode<Item> ptr = sentinel.next;
        while (ptr != sentinel) {
            System.out.print(ptr.L + "");
            ptr = ptr.next;
        }
        System.out.println();
    }

    public Item removeFirst() {
        if (size == 0) return null;
        LinkedNode<Item> saving = sentinel.next;
        sentinel.next = saving.next;
        saving.next.front = sentinel;
        size--;
        return saving.L;
    }

    public Item removeLast() {
        if (size == 0) return null;
        LinkedNode<Item> saving = sentinel.front;
        sentinel.front = saving.front;
        saving.front.next = sentinel;
        size--;
        return saving.L;
    }

    public Item get(int index) {
        LinkedNode<Item> search = sentinel;
        for (int i = 0; i <= index; i++) {
            search = search.next;
            if (search == sentinel) return null;
        }
        return search.L;
    }
    public Item getRecursive(int index)
    {
        return null;
    }

    public Iterator<Item> interator() {
        return null;
    }

    public boolean equals(Object o) {
        return true;
    }

    public static class LinkedNode<Item> {
        Item L = null;
        private LinkedNode front = null;
        private LinkedNode next = null;
    }


}
