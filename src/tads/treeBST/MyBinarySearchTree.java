package tads.treeBST;

import tads.LinkedList.MyList;

public interface MyBinarySearchTree<K extends Comparable<K>, T> {

    void insert (K key, T value);

    void delete (K key);

    boolean contains(K key);

    T find(K key);

    int size();

    int countLeaf();

    int countCompleteElement();

    MyList<T> inOrder();

    MyList<T> preOrder();

    MyList<T> postOrder();

    MyList<T> byLevel();

}