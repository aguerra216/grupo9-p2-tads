package tads.LinkedList;

import java.util.Iterator;

public class MyLinkedListIterator<T> implements Iterator<T> {


    private Node<T> node;

    public MyLinkedListIterator(Node<T> node) {
        this.node = node;
    }


    @Override
    public boolean hasNext() {
        return (node != null);
    }

    @Override
    public T next() {
        T valueToReturn = node.getValue();
        node = node.getNext();
        return valueToReturn;
    }
}