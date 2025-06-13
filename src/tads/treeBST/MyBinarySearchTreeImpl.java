package tads.treeBST;

import tads.LinkedList.*;
import tads.queue.MyQueue;
import tads.queue.MyQueueImpl;
/*public class MyBinarySearchTreeImpl<K extends Comparable<K>, T>
        implements MyBinarySearchTree<K,T> {



    private TreeNode<K, T> root;

    public MyBinarySearchTreeImpl() {
        this.root = null;
    }


    @Override
    public void insert(K key, T value) {
        this.root = insert(key, value, this.root);
    }

    @Override
    public void delete (K key){ this.root = delete(key, this.root); }

    @Override
    public boolean contains(K key) {
        return contains(key, this.root);
    }

    @Override
    public T find(K key) {
        return find(key, this.root);
    }

    @Override
    public int size(){ return size(this.root); }

    @Override
    public int countLeaf() {
        return countLeaf(this.root);
    }

    @Override
    public int countCompleteElement() {
        return countCompleteElement(this.root);
    }

    @Override
    public MyList<T> inOrder() {
        MyList<T> inOrderList = new MyLinkedListImpl<>();
        return inOrder(this.root, inOrderList);
    }

    @Override
    public MyList<T> preOrder() {
        MyList<T> preOrderList = new MyLinkedListImpl<>();
        return preOrder(this.root, preOrderList);
    }

    @Override
    public MyList<T> postOrder() {
        MyList<T> postOrderList = new MyLinkedListImpl<>();
        return postOrder(this.root, postOrderList);
    }

    @Override
    public MyList<T> byLevel() {
        MyList<T> listaNivel = new MyLinkedListImpl<>();
        MyQueue<TreeNode<K,T>> listaSubTree = new MyQueueImpl<>();
        listaSubTree.enqueue(this.root);
        while(! (listaSubTree.isEmpty())){
            TreeNode<K,T> temp = null;
            try {
                temp = listaSubTree.dequeue();
            } catch (Exception e) {
                System.out.println(e.getMessage());;
            }
            listaNivel.add(temp.getValue());
            if(temp.getLeft() != null){
                listaSubTree.enqueue(temp.getLeft());
            }
            if(temp.getRight() != null){
                listaSubTree.enqueue(temp.getRight());
            }
        }

        return listaNivel;
    }




    private TreeNode<K, T> insert(K key, T value, TreeNode<K, T> subTree) {

        if (subTree == null){
            TreeNode<K, T> newNode = new TreeNode(key, value);
            subTree = newNode;

        } else {
            if (key.compareTo(subTree.getKey()) > 0){
                subTree.setRight(insert(key, value, subTree.getRight()));
            } else if (key.compareTo(subTree.getKey()) < 0){
                subTree.setLeft(insert(key, value, subTree.getLeft()));
            } else{
                // Se asume que no deberian haber 2 keys iguales
            }
        }
        return subTree;
    }

    private TreeNode<K,T> delete(K key, TreeNode<K,T> subtree) {
        TreeNode<K,T> subtreeToReturn = subtree;

        if (key.compareTo(subtree.getKey()) == 0){
            if (subtree.getLeft() == null && subtree.getRight() == null){
                subtreeToReturn = null;
            } else if(subtree.getRight() == null){
                subtreeToReturn = subtree.getLeft();
            } else if(subtree.getLeft() == null){
                subtreeToReturn = subtree.getRight();
            } else {
                TreeNode<K,T> min = findMax(subtree.getLeft());
                subtree.setKey(min.getKey());
                subtree.setValue(min.getValue());
                subtree.setLeft(delete(min.getKey(), subtree.getLeft()));
            }
        } else if (key.compareTo(subtree.getKey()) > 0){
            subtree.setRight(delete(key, subtree.getRight()));
        } else{
            subtree.setLeft(delete(key, subtree.getLeft()));
        }

        return subtreeToReturn;
    }

    private boolean contains(K key, TreeNode<K, T> subTree) {
        boolean result = false;
        if (subTree != null) {
            if(key.compareTo(subTree.getKey()) == 0){
                result = true;
            } else if (key.compareTo(subTree.getKey()) > 0){
                result = contains(key, subTree.getRight());
            } else {
                result = contains(key, subTree.getLeft());
            }
        }
        return result;
    }

    private T find(K key, TreeNode<K, T> subTree) {
        T result = null;
        if (subTree != null) {
            if(key.compareTo(subTree.getKey()) == 0){
                result = subTree.getValue();
            } else if (key.compareTo(subTree.getKey()) > 0){
                result = find(key, subTree.getRight());
            } else {
                result = find(key, subTree.getLeft());
            }
        }
        return result;
    }

    private int size(TreeNode<K,T> subtree) {
        int size = 0;
        if (subtree != null){
            size++;
            if (subtree.getLeft() != null){
                size = size + size(subtree.getLeft());
            }
            if(subtree.getRight() != null){
                size = size + size(subtree.getRight());
            }
        }
        return size;
    }

    private int countLeaf(TreeNode<K, T> subtree){
        int leafs = 0;
        if (subtree != null){
            if (subtree.getRight() != null || subtree.getLeft() != null){
                if (subtree.getRight() != null){
                    leafs = leafs + countLeaf(subtree.getRight());
                }
                if (subtree.getLeft() != null){
                    leafs = leafs + countLeaf(subtree.getLeft());
                }
            } else{
                leafs++;
            }
        }
        return leafs;
    }

    private int countCompleteElement(TreeNode<K,T> subtree){

        int completeElement = 0;
        if(subtree != null){
            if (subtree.getLeft() != null ){
                completeElement = completeElement + countCompleteElement(subtree.getLeft());
            }
            if (subtree.getRight() != null){
                completeElement = completeElement + countCompleteElement(subtree.getRight());
            }
            if (subtree.getLeft() != null && subtree.getRight() != null){
                completeElement++;
            }
        }
        return completeElement;
    }

    private MyList<T> postOrder(TreeNode<K, T> subtree, MyList<T> postOrderList) {

        if (subtree != null){
            if (subtree.getLeft() != null){
                postOrder(subtree.getLeft(), postOrderList);
            }
            if (subtree.getRight() != null){
                postOrder(subtree.getRight(), postOrderList);
            }
            postOrderList.add(subtree.getValue());
        }
        return postOrderList;

    }

    private MyList<T> preOrder(TreeNode<K, T> subtree, MyList<T> preOrderList) {

        if (subtree != null){
            preOrderList.add(subtree.getValue());
            if (subtree.getLeft() != null){
                preOrder(subtree.getLeft(), preOrderList);
            }
            if (subtree.getRight() != null){
                preOrder(subtree.getRight(), preOrderList);
            }
        }
        return preOrderList;
    }

    private MyList<T> inOrder(TreeNode<K,T> subtree, MyList<T> inOrderList) {


        if (subtree != null){
            if (subtree.getLeft() != null){
                inOrder(subtree.getLeft(), inOrderList);
            }
            inOrderList.add(subtree.getValue());
            if (subtree.getRight() != null){
                inOrder(subtree.getRight(), inOrderList);
            }
        }
        return inOrderList;
    }

    private TreeNode<K,T> findMax(TreeNode<K,T> subtree) {
        TreeNode<K,T> treeToReturn = null;

        if (subtree != null) {
            if (subtree.getRight() == null) {
                treeToReturn = subtree;
            } else{
                treeToReturn = findMax(subtree.getRight());
            }
        }
        return treeToReturn;
    }
}*/