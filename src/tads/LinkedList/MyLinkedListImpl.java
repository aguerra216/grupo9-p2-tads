package tads.LinkedList;

import exceptions.EmptyQueueException;
import exceptions.EmptyStackException;

public class MyLinkedListImpl<T> implements MyList<T> {
    private Node<T> first;
    private Node<T> last;

    public MyLinkedListImpl() {
        this.first = null;
        this.last = null;
    }

    @Override
    public Node<T> first() {
        return first;
    }

    @Override
    public void add(T value) {
        addToTheEnd(value);
    }

    private void addToBeginning(T value) {
        if (value != null) {

            Node<T> elementToAdd = new Node<>(value);

            if (this.first == null) {

                this.first = elementToAdd;
                this.last = elementToAdd;

            } else {

                elementToAdd.setNext(this.first);
                this.first = elementToAdd;
            }

        } else {

        }
    }

    private void addToTheEnd(T value) {
        if (value != null) {

            Node<T> elementToAdd = new Node<>(value);

            if (this.first == null) {

                this.first = elementToAdd;
                this.last = elementToAdd;

            } else {

                this.last.setNext(elementToAdd);
                this.last = elementToAdd;
            }

        } else {

        }
    }

    public T get(int position) {
        T valueToReturn = null;
        int tempPosition = 0;
        Node<T> temp = this.first;


        while (temp != null && tempPosition != position) {

            temp = temp.getNext();
            tempPosition++;

        }

        if (tempPosition == position) {


            valueToReturn = temp.getValue();

        }

        return valueToReturn;
    }

    public boolean contains(T value) {
        boolean contains = false;
        Node<T> temp = this.first;

        while (temp != null && !temp.getValue().equals(value)) {

            temp = temp.getNext();

        }

        if (temp != null) {

            contains = true;

        }

        return contains;
    }

    public void remove(T value) {
        Node<T> beforeSearchValue = null;
        Node<T> searchValue = this.first;


        while (searchValue != null && !searchValue.getValue().equals(value)) {

            beforeSearchValue = searchValue;
            searchValue = searchValue.getNext();

        }

        if (searchValue != null) {

            if (searchValue == this.first && searchValue != this.last) {

                Node<T> temp = this.first;
                this.first = this.first.getNext();

                temp.setNext(null);


            } else if (searchValue == this.last && searchValue != this.first) {

                beforeSearchValue.setNext(null);
                this.last = beforeSearchValue;


            } else if (searchValue == this.last && searchValue == this.first) {

                this.first = null;
                this.last = null;

            } else {

                beforeSearchValue.setNext(searchValue.getNext());
                searchValue.setNext(null);

            }

        } else {

        }

    }

    private T removeLast() {
        T valueToRemove = null;

        if (this.last != null) {
            valueToRemove = this.last.getValue();

            remove(valueToRemove);
        }

        return valueToRemove;
    }

    public int size() {
        int size = 0;

        Node<T> temp = this.first;

        while (temp != null) {

            temp = temp.getNext();
            size++;

        }

        return size;
    }



    public void enqueue(T value) {
        addToBeginning(value);
    }

    public T dequeue() throws EmptyQueueException {
        if (this.last == null) {

            throw new EmptyQueueException("List is empty");
        }

        return removeLast();
    }



    public void push(T value) {
        addToTheEnd(value);
    }

    public T pop() throws EmptyStackException {
        if (this.last == null) {

            throw new EmptyStackException("Stack is empty");
        }

        return removeLast();
    }

    public T peek() {
        T valueToReturn = null;

        if (this.last != null) {
            valueToReturn = this.last.getValue();
        }

        return valueToReturn;
    }

    public boolean isEmpty() {
        return (this.first == null && this.last==null);
    }
}
