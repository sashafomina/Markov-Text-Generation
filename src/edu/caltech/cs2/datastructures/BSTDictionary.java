package edu.caltech.cs2.datastructures;



import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IDictionary;
import edu.caltech.cs2.interfaces.IQueue;

import java.util.Iterator;

public class BSTDictionary<K extends Comparable<K>, V> implements IDictionary<K, V> {

    private static int LEFT = 0;
    private static int RIGHT = 1;

    protected BSTNode<K, V> root;
    protected int size;


    private class BSTIterator<K> implements Iterator<K>{
        private int currentSize = 0;
        private Iterator<K> keySet = null;

        public boolean hasNext(){
            return this.currentSize < BSTDictionary.this.size;
        }

        public K next() {
            if (this.currentSize == 0){
                this.keySet = ((ICollection<K>) BSTDictionary.this.keySet()).iterator();
            }
            K next = this.keySet.next();
            this.currentSize++;
            return next;
        }

    }

    @Override
    public V get(K key) {
        if (!containsKey(key)){
            return null;
        }
        else {
            return get(this.root, key);
        }
    }

    private V get(BSTNode<K,V> curr, K key){
        if (curr.key.equals(key)){
            return curr.value;
        }
        else if (curr.key.compareTo(key) < 0){
            return get(curr.children[RIGHT], key);
        }
        else {
            return get(curr.children[LEFT], key);
        }
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)){
            return null;
        }
        IDeque<V> old = new LinkedDeque<>();
        this.root = remove(this.root, key, old);
        this.size--;
        return old.peekFront();

    }


    private BSTNode<K,V> remove(BSTNode<K,V> curr, K key, IDeque<V> old){
        if (curr.key.equals(key)){
            old.add(curr.value);
            if(curr.isLeaf()){
              curr = null;
            }
            else if (curr.children[LEFT] == null){
                curr = curr.children[RIGHT];
            }
            else if (curr.children[RIGHT] == null){
                curr = curr.children[LEFT];
            }
            else{
                BSTNode<K,V> replacement = findMin(curr.children[RIGHT]);
                remove(replacement.key);
                this.size++;
                replacement.children[LEFT] = curr.children[LEFT];
                replacement.children[RIGHT] = curr.children[RIGHT];
                curr = replacement;
            }
        }
        else if (curr.key.compareTo(key) < 0){
            curr.children[RIGHT] = remove(curr.children[RIGHT], key, old);
        }
        else {
            curr.children[LEFT] = remove(curr.children[LEFT], key, old);
        }
        return curr;
    }



    private BSTNode<K,V> findMin(BSTNode<K,V> minNode){
        if (minNode == null || minNode.children[LEFT] == null){
            return minNode;
        }
        else {
            return findMin(minNode.children[LEFT]);
        }

    }
    @Override
    public V put(K key, V value) {
        //System.out.println("BEFORE REMOVE: " + this.size);
        V old = this.remove(key);
        //System.out.println("AFTER REMOVE: " + this.size);
        this.size++;
        //System.out.println("AFTER ADD BACK: " + this.size);
        this.root = put(this.root, key, value);
        return old;
    }

    private BSTNode<K,V> put(BSTNode<K,V> curr, K key, V value){
        if (curr == null){
            curr = new BSTNode<>(key, value);
        }

        else if (curr.key.compareTo(key) > 0){
            curr.children[LEFT] = put(curr.children[LEFT], key, value);
        }
        else{
            curr.children[RIGHT] = put(curr.children[RIGHT], key,value);
        }
        return curr;
    }


    @Override
    public boolean containsKey(K key) {
        return containsKey(this.root, key);
    }

    private boolean containsKey(BSTNode<K,V> curr, K key){
        if (curr == null){
            return false;
        }
        else if (curr.key.equals(key)){
            return true;
        }
        else if (curr.key.compareTo(key) < 0){
            return containsKey(curr.children[RIGHT], key);
        }
        else {
            return containsKey(curr.children[LEFT], key);
        }
    }

    @Override
    public boolean containsValue(V value) {
        return containsValue(this.root, value);
    }

    private boolean containsValue(BSTNode curr, V value){
        if (curr == null){
            return false;
        }
        else if (curr.value.equals(value)){
            return true;
        }
        else{
            return containsValue(curr.children[LEFT], value) || containsValue(curr.children[RIGHT], value);
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> keys = new LinkedDeque<>();
        keySet(this.root, keys);
        return keys;
    }

    private void keySet(BSTNode<K,V> curr, ICollection<K> keys){
        if (curr != null){
            keys.add(curr.key);
            keySet(curr.children[LEFT], keys);
            keySet(curr.children[RIGHT], keys);
        }
    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque<>();
        values(this.root, values);
        return values;
    }

    private void values(BSTNode<K,V> curr, ICollection<V> vals){
        if (curr != null){
            vals.add(curr.value);
            values(curr.children[LEFT], vals);
            values(curr.children[RIGHT], vals);
        }
    }

    @Override
    public Iterator<K> iterator() {
        return new BSTIterator<K>();
    }
    
    @Override
    public String toString() {
        if (this.root == null) {
            return "{}";
        }

        StringBuilder contents = new StringBuilder();

        IQueue<BSTNode<K, V>> nodes = new ArrayDeque<>();
        BSTNode<K, V> current = this.root;
        while (current != null) {
            contents.append(current.key + ": " + current.value + ", ");

            if (current.children[0] != null) {
                nodes.enqueue(current.children[0]);
            }
            if (current.children[1] != null) {
                nodes.enqueue(current.children[1]);
            }

            current = nodes.dequeue();
        }

        return "{" + contents.toString().substring(0, contents.length() - 2) + "}";
    }

    protected static class BSTNode<K, V> {
        public final K key;
        public final V value;
        public BSTNode<K, V>[] children;

        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.children = (BSTNode<K, V>[]) new BSTNode[2];
        }

        public BSTNode(BSTNode<K, V> o) {
            this.key = o.key;
            this.value = o.value;
            this.children = (BSTNode<K, V>[]) new BSTNode[2];
            this.children[LEFT] = o.children[LEFT];
            this.children[RIGHT] = o.children[RIGHT];
        }

        public boolean isLeaf() {
            return this.children[LEFT] == null && this.children[RIGHT] == null;
        }

        public boolean hasBothChildren() {
            return this.children[LEFT] != null && this.children[RIGHT] != null;
        }
    }
}
