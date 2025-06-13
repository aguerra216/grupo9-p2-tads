package tads.queue;
public interface MyQueue<T> extends Iterable<T> {

    void enqueue(T value);

    T peek();

    void enqueueWithPriority(T value);

    T dequeue();

    boolean isEmpty();

    int getSize();

}


