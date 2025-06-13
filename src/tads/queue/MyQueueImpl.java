package tads.queue;

import tads.LinkedList.*;

import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.Iterator;

public class MyQueueImpl<T> implements MyQueue<T>{

    private Node<T> head;
    private Node<T> tail;
    private int size;
    private Comparator<T> comparator;


    public MyQueueImpl(Comparator<T> comparator) {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.comparator = comparator;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new MyLinkedListIterator<T>(head);
    }



    public T remove(int index) {

        T toReturn = null;
        if (index == size-1){                                //En este caso se elimina el ultimo indirectamente
            toReturn =  removeLast();
        } else if (index == 0){
            toReturn = head.getValue();
            Node<T> newHead = head.getNext();
            head.setNext(null);
            head = newHead;
            size--;
        } else {
            Node<T> temp = nodeAtIndex(index - 1);     //Busca el anterior del que quiero elminar
            Node<T> nodeToReturn = temp.getNext();                //Encuentro el que quiero eliminar
            toReturn = nodeToReturn.getValue();
            temp.setNext(nodeToReturn.getNext());         //Le doy al nodo anterior el siguiente del que voy  eliminar
            nodeToReturn.setNext(null);
            size--;
        }
        return toReturn;
    }
    public T removeLast() {
        T toReturn = null;

        if (size > 0) {
            toReturn = tail.getValue();
            if (size == 1) {                                 //Si hay 1 solo elemento lo elimina a ese
                head = null;
                tail = null;
                size--;
            } else {
                Node<T> temp = null;
                try {
                    temp = nodeAtIndex(size-2);
                    temp.setNext(null);
                    tail = temp;
                    size--;                     //Va al penultimo nodo y elimina, sabiendo que no hay forma que de error
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return toReturn;
    }
    private  Node<T> nodeAtIndex(int index){
        Node<T> temp = null;
        if (index >= size || index < 0) {          //Ceckea que exista la pos
            throw  new IndexOutOfBoundsException();
        } else if (index == (size-1)){                //Checkea si es el ultimo
            temp = tail;
        } else {                                        //Recorre la cantidad de veces necesarias y devuelve el ultimo
            temp = head;
            for (int i = 0; i < index ; i++){
                temp = temp.getNext();
            }
        }
        return temp;
    }
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public void makeEmpty() {

        Node<T> temp = this.head;
        Node<T> nextNode = temp.getNext();

        while(nextNode != null){
            temp.setNext(null);
            temp = nextNode;
            nextNode = temp.getNext();
        }
        head = null;
        tail = null;
        size = 0;
    }
    private void addLast(T valor){                         //Agrega al final
        if (head == null){                   //Si la lista es vacia agrega al principio
            addFirst(valor);
        } else {
            Node<T> newNode = new Node<T>(valor);   //Si la lista no es vacia, solamente agrega seteando el siguiente del
            tail.setNext(newNode);     //ultimo y renueva el ultimo
            tail = newNode;
            size++;
        }
    }
    public void addFirst(T value) {
        Node<T> newNode = new Node<T>(value);

        if (head == null) {
            head = newNode;
            size++;
            tail = newNode;
        } else {
            newNode.setNext(head);
            head = newNode;
            size++;
        }
    }

    @Override
    public void enqueue(T value) {
        addLast(value);
    }

    @Override
    public void enqueueWithPriority(T value) {
        addInOrder(value);
    }

    @Override
    public T dequeue()  {
        Node<T> toReturn = this.head;

        if(size == 0){
            throw new EmptyStackException();
        } else{
            try {
                remove(0);
            } catch (Exception e) {
                System.out.println(e.getMessage());;
            }
        }
        return toReturn.getValue();
    }

    private void addInOrder(T value) {
        if (size == 0) {
            addFirst(value);
        } else if (comparator != null) {
            if (comparator.compare(head.getValue(), value) > 0) {
                addFirst(value);
            } else {
                Node<T> temp = head.getNext();
                Node<T> previous = head;
                boolean added = false;

                while (temp != null) {
                    if (comparator.compare(temp.getValue(), value) > 0) {
                        Node<T> newNode = new Node<>(value);
                        previous.setNext(newNode);
                        newNode.setNext(temp);
                        added = true;
                        break;
                    }
                    previous = temp;
                    temp = temp.getNext();
                }

                if (!added) {
                    addLast(value);
                } else {
                    size++; // Increment only if inserted in the middle
                }
            }
        } else {
            addLast(value);
        }
    }

    @Override
    public T peek() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        return head.getValue();
    }
}
