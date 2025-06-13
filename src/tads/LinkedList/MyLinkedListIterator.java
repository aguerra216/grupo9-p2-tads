package tads.LinkedList;
import java.util.NoSuchElementException;
import java.util.Iterator;

public class MyLinkedListIterator<T> implements Iterator<T> {
    private Node<T> current;

    public MyLinkedListIterator(Node<T> first) {
        this.current = first;
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T value = current.getValue();
        current = current.getNext();
        return value;
    }
}