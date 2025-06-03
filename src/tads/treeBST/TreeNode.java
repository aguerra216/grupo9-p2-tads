package tads.treeBST;
public class TreeNode<K, T> {


    private T value;

    private K key;

    private TreeNode<K, T> right;

    private TreeNode<K, T> left;

    public TreeNode(K key, T value) {
        this.value = value;
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public TreeNode<K, T> getRight() {
        return right;
    }

    public void setRight(TreeNode<K, T> right) {
        this.right = right;
    }

    public TreeNode<K, T> getLeft() {
        return left;
    }

    public void setLeft(TreeNode<K,T> left) {
        this.left = left;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }
}
