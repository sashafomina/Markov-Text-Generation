package edu.caltech.cs2.datastructures;
import edu.caltech.cs2.interfaces.ICollection;
import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.ITrieMap;

import java.util.function.Function;
import java.util.Iterator;

public class TrieMap<A, K extends Iterable<A>, V> extends ITrieMap<A, K, V> {

    public TrieMap(Function<IDeque<A>, K> collector) {
        super(collector);
    }

    @Override
    public boolean isPrefix(K key) {
        TrieNode curr = this.root;
        for (A letter : key){
            if (!(curr.pointers.containsKey(letter))){
                return false;
            }
            curr = (TrieNode) curr.pointers.get(letter);
        }
        return true;
    }

    @Override
    public IDeque<V> getCompletions(K prefix) {
        if (!isPrefix(prefix)){
            return new LinkedDeque<>();
        }
        Iterator<A> prefixItr = prefix.iterator();
        IDeque<V> comp = new LinkedDeque<>();
        getCompletions(prefixItr, this.root, comp);
        return comp;
    }


    private void getCompletions(Iterator<A> prefix, TrieNode<A,V> curr, IDeque<V> completions) {
        if (curr != null && curr.pointers.size() == 0 ){//&& !(completions.contains(curr.value))){
            completions.add(curr.value);
        }
        else if (curr != null && prefix.hasNext()) {
            A next = prefix.next();
            getCompletions(prefix, curr.pointers.get(next), completions);
        }
        else if (curr != null) {
            for (A key : curr.pointers.keySet()) {
                getCompletions(prefix, curr.pointers.get(key), completions);
            }
            if (curr.value != null ){
                completions.add(curr.value);
            }
        }

    }


    @Override
    public V get(K key) {
        if (! containsKey(key)){
            return null;
        }
        TrieNode<A, V> curr = this.root;
        for (A letter: key){
            curr = curr.pointers.get(letter);
        }
        return curr.value;
    }

    @Override
    public V remove(K key) {
        if (containsKey(key)){
            this.size--;
            return remove(key.iterator(), this.root, null, null);
        }
        else {
            return null;
        }
    }

    private V remove (Iterator<A> keyItr , TrieNode<A,V> curr, TrieNode<A,V> prev, A key) {
        if (keyItr.hasNext()){
            A next = keyItr.next();
            V old = remove(keyItr, curr.pointers.get(next), curr, next);
            if (curr.value == null && curr.pointers.size() == 0 ){
                prev.pointers.remove(key);
                return old;
            }
            else{
                return old;
            }
        }
        else if (curr.pointers.size() == 0){
            V old = curr.value;
            prev.pointers.remove(key);
            return old;
        }
        else{
            V old = curr.value;
            curr.value = null;
            return old;
        }
    }


    @Override
    public V put(K key, V value) {
        Iterator<A> keyItr = key.iterator();
        V old = put(keyItr, value, this.root);
        return old;
    }

    private V put(Iterator<A> key, V value, TrieNode<A,V> curr){
        V old = null;
        if (curr != null) {
            if (key.hasNext()) {
                A next = key.next();
                curr.pointers.putIfAbsent(next, new TrieNode<>());
                put(key, value, curr.pointers.get(next));
            }
            else {
                old = curr.value;
                curr.value = value;
                if (old == null) {
                    this.size++;
                }
            }
        }
        return old;
    }



    @Override
    public boolean containsKey(K key) {
        TrieNode<A, V> curr = this.root;
        for (A letter : key){
            if (!(curr.pointers.containsKey(letter))){
                return false;
            }
            curr = curr.pointers.get(letter);
        }
        if (curr.value == null){
            return false;
        }
        return true;
    }


    @Override
    public boolean containsValue(V value) {
        return containsValue(value, this.root);

    }

    private boolean containsValue(V value, TrieNode<A,V> curr){
        if (curr == null){
            return false;
        }
        else if (curr.value == value){
            return true;
        }
        for (A key : curr.pointers.keySet()){
            if (containsValue(value, curr.pointers.get(key))){
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public ICollection<K> keySet() {
        ICollection<K> ans = new LinkedDeque<>();
        IDeque<A> accu = new LinkedDeque<>();
        keySet(accu, this.root, ans);
        return ans;
    }

    private void keySet(IDeque<A> accumulator, TrieNode<A,V> curr, ICollection<K> ans){
        if(curr != null && curr.value != null && curr.pointers.size() == 0){
            ans.add(this.collector.apply(accumulator));
            //accumulator.removeBack();
        }
        else{
            for ( A key : curr.pointers.keySet()){
                accumulator.addBack(key);
                keySet(accumulator, curr.pointers.get(key), ans);
                accumulator.removeBack();
            }
            if (curr.value != null){
                ans.add(this.collector.apply(accumulator));
            }
        }

    }

    @Override
    public ICollection<V> values() {
        ICollection<V> values = new LinkedDeque<>();
        values(this.root, values);
        return values;
    }

    private void values(TrieNode<A,V> curr, ICollection<V> accumulator){
        if (curr != null && curr.pointers.size() == 0 && !(accumulator.contains(curr.value))){
            accumulator.add(curr.value);
        }
        else if (curr != null) {
            for (A key : curr.pointers.keySet()){
                values(curr.pointers.get(key), accumulator);
                if (curr.value != null && !(accumulator.contains(curr.value))){
                    accumulator.add(curr.value);
                }
            }
        }
    }
    @Override
    public Iterator<K> iterator() {
        //System.out.println(keySet());
        return keySet().iterator();
    }
}