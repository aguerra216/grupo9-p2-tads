package tads.HashT;
import tads.LinkedList.MyLinkedListImpl;
import tads.LinkedList.MyList;

public class MyHashMap<Key, Value> implements HashInter<Key, Value> {
    private static class Node<Key, Value> {
        Key key;
        Value value;

        Node(Key key, Value value) {
            this.key = key;
            this.value = value;
        }
    }

    private MyList<Node<Key, Value>>[] table;
    private int capacity;
    private int size;

    public MyHashMap(int capacity) {
        this.capacity = capacity;
        this.table = new MyList[capacity];
        this.size = 0;

        for (int i = 0; i < capacity; i++) {
            table[i] = new MyLinkedListImpl<>();
        }
    }

    private int hash(Key key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    @Override
    public void put(Key key, Value value) {
        int index = hash(key);
        MyList<Node<Key, Value>> bucket = table[index];

        for (int i = 0; i < bucket.size(); i++) {
            Node<Key, Value> node = bucket.get(i);
            if (node.key.equals(key)) {
                node.value = value;
                return;
            }
        }

        bucket.add(new Node<>(key, value));
        size++;
    }

    @Override
    public Value get(Key key) {
        int index = hash(key);
        MyList<Node<Key, Value>> bucket = table[index];

        for (int i = 0; i < bucket.size(); i++) {
            Node<Key, Value> node = bucket.get(i);
            if (node.key.equals(key)) {
                return node.value;
            }
        }

        return null;
    }

    @Override
    public boolean contains(Key key) {
        return get(key) != null;
    }

    @Override
    public void remove(Key key) {
        int index = hash(key);
        MyList<Node<Key, Value>> bucket = table[index];

        for (int i = 0; i < bucket.size(); i++) {
            Node<Key, Value> node = bucket.get(i);
            if (node.key.equals(key)) {
                bucket.remove(node);
                size--;
                return;
            }
        }
    }

    @Override

    public MyLinkedListImpl<Key> keys() {
        MyLinkedListImpl<Key> keys = new MyLinkedListImpl<>();
        for (int i = 0; i < capacity; i++) {
            MyList<Node<Key, Value>> bucket = table[i];
            for (int j = 0; j < bucket.size(); j++) {
                keys.add(bucket.get(j).key);
            }
        }
        return keys;
    }

    @Override
    public MyLinkedListImpl<Value> values() {
        MyLinkedListImpl<Value> values = new MyLinkedListImpl<>();
        for (int i = 0; i < capacity; i++) {
            MyList<Node<Key, Value>> bucket = table[i];
            for (int j = 0; j < bucket.size(); j++) {
                values.add(bucket.get(j).value);
            }
        }
        return values;
    }

    @Override
    public int size() {
        return size;
    }
}

