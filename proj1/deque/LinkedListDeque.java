package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    private LinkedNode<T> sentinel = new LinkedNode<T>();
    private int size = 0;

    public LinkedListDeque() {

        sentinel.front = sentinel;
        sentinel.next = sentinel;

    }

    @Override
    public Iterator<T> iterator() {
        return new theLinked();
    }

    @Override
    public void addFirst(T L) {
        LinkedNode<T> newOne = new LinkedNode<T>();
        newOne.L = L;
        newOne.next = sentinel.next;
        newOne.front = sentinel;
        sentinel.next.front = newOne;
        sentinel.next = newOne;
        size++;
    }

    @Override
    public void addLast(T L) {
        LinkedNode<T> newOne = new LinkedNode<T>();
        newOne.L = L;
        newOne.next = sentinel;
        newOne.front = sentinel.front;
        sentinel.front.next = newOne;
        sentinel.front = newOne;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int flag = 0;
        LinkedNode<T> ptr = sentinel.next;
        while (ptr != sentinel) {
            if (flag == 0) {
                flag = 1;
                System.out.print(ptr.L);
            } else {
                System.out.print(" " + ptr.L);
            }
            ptr = ptr.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) return null;
        LinkedNode<T> saving = sentinel.next;
        sentinel.next = saving.next;
        saving.next.front = sentinel;
        size--;
        return saving.L;
    }

    @Override
    public T removeLast() {
        if (size == 0) return null;
        LinkedNode<T> saving = sentinel.front;
        sentinel.front = saving.front;
        saving.front.next = sentinel;
        size--;
        return saving.L;
    }


    @Override
    public T get(int index) {
        if (index < 0 || index >= size) return null;  // 允许从0到size-1
        LinkedNode<T> search = sentinel.next;
        for (int i = 0; i < index; i++) {  // 遍历直到目标节点
            search = search.next;
        }
        return search.L;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != LinkedListDeque.class) return false;

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(((LinkedListDeque<T>) o).get(i))) return false;
        }
        return true;
    }

    private class theLinked implements Iterator<T> {
        public LinkedNode<T> current;

        public theLinked() {
            this.current = sentinel.next;
        }

        @Override
        public boolean hasNext() {
           return current != sentinel;
        }

        @Override
        public T next() {
            T ONE = current.L;
            current = current.next;
            return ONE;
        }
    }

    public class LinkedNode<T> {
        T L = null;
        private LinkedNode<T> front = null;
        private LinkedNode<T> next = null;
    }
}
