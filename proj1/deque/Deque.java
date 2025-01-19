package deque;

import java.util.Iterator;

public interface Deque<T> {

    public Iterator<T> iterator();

    public void addFirst(T L);

    public void addLast(T L);

    default boolean isEmpty(){
        if (size() == 0) return true;
        return false;
    }

    public int size();

    public void printDeque();

    public T removeFirst();

    public T removeLast();

    public T get(int index);

    public boolean equals(Object o);
}
