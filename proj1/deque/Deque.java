package deque;

import java.util.Iterator;

public interface Deque<Item> {

    public Iterator<Item> iterator();

    public void addFirst(Item L);

    public void addLast(Item L);

    default boolean isEmpty(){
        if (size() == 0) return true;
        return false;
    }

    public int size();

    public void printDeque();

    public Item removeFirst();

    public Item removeLast();

    public Item get(int index);

    public boolean equals(Object o);
}
