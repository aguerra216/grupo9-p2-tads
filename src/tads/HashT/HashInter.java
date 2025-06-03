package tads.HashT;

import tads.LinkedList.MyList;

public interface HashInter<Key,Value> {
    void put(Key key, Value value);
    Value get(Key key);
    boolean contains(Key key);
    void remove(Key key);
    MyList<Key> keys();
    MyList<Value> values();
    int size();




}
