package deque;

import java.util.Iterator;

public class LinkedListDeque<Item> implements Iterable<Item> ,Deque<Item>{

    public LinkedNode<Item> sentinel = new LinkedNode<Item>();
    private int size = 0;

    public LinkedListDeque() {

        sentinel.front = sentinel;
        sentinel.next = sentinel;

    }

    @Override
    public Iterator<Item> iterator() {
        return new theLinked();
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


    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != LinkedListDeque.class) return false;

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(((LinkedListDeque<Item>) o).get(i))) return false;
        }
        return true;
    }

    private class theLinked implements Iterator<Item> {
        public LinkedNode<Item> current;

        public theLinked() {
            this.current = sentinel.next;
        }

        @Override
        public boolean hasNext() {
            if (current == sentinel) return false;
            return true;
        }

        @Override
        public Item next() {
            Item ONE = current.L;
            current = current.next;
            return ONE;
        }
    }

    public class LinkedNode<Item> {
        Item L = null;
        private LinkedNode front = null;
        private LinkedNode next = null;

    }


}
