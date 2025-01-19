package deque;

import java.util.Iterator;

public interface List<Item> {

    public void addFirst(Item L);

    public void addLast (Item L);

    public boolean isEmpty();

    public int size();

    public void printDeque();

    public Item removeFirst();

    public Item removeLast();

    public Item get (int indext);


    public Iterator<Item> interator();

    public boolean equals(Object o);
}
