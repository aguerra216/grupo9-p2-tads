package tads.LinkedList;

import java.util.Iterator;

public interface MyList<T> {
    void add(T value);

    T get(int position);

    boolean contains(T value);

    void remove(T value);

    int size();

    Node<T> first();

    boolean isEmpty();

    Iterator<T> iterator(); //ver si se puede
}
