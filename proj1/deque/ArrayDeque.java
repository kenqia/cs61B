package deque;

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
        if (size + 1 >=items.length ) resize(2 * size );
        System.arraycopy(items, 0, items, 1, size);
        size++;
        items[0] = L;
    }

    @Override
    public void addLast(T L) {
        if (size + 1 >=items.length ) resize(2 * size );
        items[size] = L;
        size++ ;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int flag = 0;
        for (int i = 0; i < size; i++) {
            if (flag == 0) {
                System.out.print(items[i]);
                flag = 1;
            } else {
                System.out.print(" " + items[i]);
            }
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if ( size == 0 ) return null;
        T saving = items[0];
        System.arraycopy(items, 1, items, 0, size - 1);
        size--;
        return saving;
    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        T saving = items[size - 1];
        size--;
        return saving;
    }
    private void resize(int newSize){
        T[] current = (T[]) new Object[newSize];
        System.arraycopy(items , 0 , current , 0 , size);
        items = current;
    }

    @Override
    public T get(int index) {
        if (index >= size || index < 0) return null;
        return items[index];
    }

    @Override
    public Iterator<T> iterator() {
        return new Arrayee();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Deque)) return false;
        if (size != ((ArrayDeque<T>) o).size) return false;
        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(((Deque<T>) o).get(i))) return false;
        }
        return true;
    }

    private class Arrayee implements Iterator<T> {
        int e;

        public Arrayee() {
            e = 0;
        }

        @Override
        public boolean hasNext() {
            if (e < size) return true;
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
