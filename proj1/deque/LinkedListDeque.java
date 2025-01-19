package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements List<Item> {

    private int size = 0;
    private LinkedNode<Item> sentinelFirst = new LinkedNode<Item>();
    private LinkedNode<Item> sentinelnext = new LinkedNode<Item>();

    public LinkedListDeque() {

        sentinelFirst.front = sentinelnext;
        sentinelFirst.next = sentinelnext;

        sentinelnext.front = sentinelFirst;
        sentinelnext.next = sentinelFirst;
    }

    public static void main(String[] args) {
        LinkedListDeque<Integer> ee = new LinkedListDeque<Integer>();
        ee.addFirst(3);
        ee.addLast(5);
    }

    public void addFirst(Item L) {
        LinkedNode<Item> newOne = new LinkedNode<Item>();
        newOne.L = L;
        newOne.next = sentinelFirst.next;
        newOne.front = sentinelFirst;
        sentinelFirst.next.front = newOne;
        sentinelFirst.next = newOne;
        size++;
    }

    public void addLast(Item L) {
        LinkedNode<Item> newOne = new LinkedNode<Item>();
        newOne.L = L;
        newOne.next = sentinelnext;
        newOne.front = sentinelnext.front;
        sentinelnext.front.next = newOne;
        sentinelnext.front = newOne;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        LinkedNode<Item> ptr = sentinelFirst.next;
        while (ptr != sentinelnext) {
            System.out.print(ptr.L + "");
            ptr = ptr.next;
        }
        System.out.println();
    }

    public Item removeFirst() {
        if (size == 0) return null;
        LinkedNode<Item> saving = sentinelFirst.next;
        sentinelFirst.next = saving.next;
        saving.next.front = sentinelFirst;
        size--;
        return saving.L;
    }

    public Item removeLast() {
        if (size == 0) return null;
        LinkedNode<Item> saving = sentinelnext.front;
        sentinelnext.front = saving.front;
        saving.front.next = sentinelnext;
        size--;
        return saving.L;
    }

    public Item get(int index) {
        LinkedNode<Item> search = sentinelFirst;
        for (int i = 0; i <= index; i++) {
            search = search.next;
            if (search == sentinelnext) return null;
        }
        return search.L;
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
