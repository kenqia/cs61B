package deque;

import java.io.ObjectStreamException;
import java.util.Arrays;
import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size = 0;

    public ArrayDeque() {
        this.items = (T[]) new Object[8];
        this.size = 0;
    }

    @Override
    public void addFirst(T L) {
        T[] newOne = (T[]) new Object[size + 1];
        newOne[0] = L;
        System.arraycopy(items, 0, newOne, 1, size);
        size++;
        items = newOne;
    }

    @Override
    public void addLast(T L) {
        T[] newOne = (T[]) new Object[size + 1];
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
        int flag = 0;
        for (int i = 0; i < size; i++) {
            if (flag == 0){
                System.out.print(items[i]);
                flag = 1;
            }else{
                System.out.print(" " + items[i]);
            }
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        T[] newOne = ( T[] ) new Object[size - 1];
        T saving = items[0];
        System.arraycopy(items, 1, newOne, 0, size - 1);
        size--;
        items = newOne;
        return saving;
    }

    @Override
    public T removeLast() {
        T[] newOne = (T[]) new Object[size - 1];
        T saving = items[size - 1];
        System.arraycopy(items, 0, newOne, 0, size - 1);
        size--;
        items = newOne;
        return saving;
    }

    @Override
    public T get(int index) {
        if (index >= size) return null;
        return items[index];
    }

    @Override
    public Iterator<T> iterator() {
        return new Arrayee();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != ArrayDeque.class) return false;

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(((ArrayDeque<T>) o).get(i))) return false;
        }
        return true;
    }

    public class Arrayee implements Iterator<T> {
        int e;

        public Arrayee() {
            e = 0;
        }

        @Override
        public boolean hasNext() {
            if(e < size) return true;
            return false;
        }

        @Override
        public T next() {
            T one = items[e];
            e++;
            return one;
        }
    }

}
