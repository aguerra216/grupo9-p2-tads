package tads.stack;

public interface  MyStack<T> extends Iterable<T> {

    public void push(T value);

    public T pop();

    public T top();

    public boolean isEmpty();

    public void makeEmpty();

    public int getSize();

}
