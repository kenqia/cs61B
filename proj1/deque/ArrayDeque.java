package deque;

import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayDeque<Item> implements Iterable<Item>, Deque<Item> {
    Item[] items;
    int size = 0;

    public ArrayDeque() {
        this.items = (Item[]) new Object[8];
        this.size = 0;
    }

    @Override
    public void addFirst(Item L) {
        Item[] newOne = (Item[]) new Object[size + 1];
        newOne[0] = L;
        System.arraycopy(items, 0, newOne, 1, size);
        size++;
        items = newOne;
    }

    @Override
    public void addLast(Item L) {
        Item[] newOne = (Item[]) new Object[size + 1];
        newOne[size] = L;
        System.arraycopy(items, 0, newOne, 0, size);
        size++;
        items = newOne;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[i]);
        }
        System.out.println();
    }

    @Override
    public Item removeFirst() {
        Item[] newOne = (Item[]) new Object[size - 1];
        Item saving = items[0];
        System.arraycopy(items, 1, newOne, 0, size - 1);
        size--;
        items = newOne;
        return saving;
    }

    @Override
    public Item removeLast() {
        Item[] newOne = (Item[]) new Object[size - 1];
        Item saving = items[size - 1];
        System.arraycopy(items, 0, newOne, 0, size - 1);
        size--;
        items = newOne;
        return saving;
    }

    @Override
    public Item get(int index) {
        if (index >= size) return null;
        return items[index];
    }

    @Override
    public Iterator<Item> iterator() {
        return new Arrayee();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != ArrayDeque.class) return false;

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(((ArrayDeque<Item>) o).get(i))) return false;
        }
        return true;
    }

    public class Arrayee implements Iterator<Item> {
        int e;

        public Arrayee() {
            e = 0;
        }

        @Override
        public boolean hasNext() {
            if (e < size) return false;
            return true;
        }

        @Override
        public Item next() {
            Item one = items[e];
            e++;
            return one;
        }
    }

}
