package edu.caltech.cs2.datastructures;


import edu.caltech.cs2.interfaces.IDeque;
import edu.caltech.cs2.interfaces.IQueue;
import edu.caltech.cs2.interfaces.IStack;

import java.util.Iterator;

public class ArrayDeque<E> implements IDeque<E>, IQueue<E>, IStack<E> {
    private E[] data;
    private int size;
    private final static int DEFAULT_CAPACITY = 10;
    private final static int GROW_FACTOR = 2;

    public ArrayDeque(){
        this(10);
    }

    public ArrayDeque(int initialCapacity){
        this.size = 0;
        this.data = (E[])new  Object[initialCapacity];
    }

    private class ADIterator implements Iterator<E> {
        private int currentIndex = 0;

        public boolean hasNext(){
            return currentIndex < ArrayDeque.this.size();
        }

        public E next(){
            E nextItem = ArrayDeque.this.data[currentIndex];
            currentIndex++;
            return nextItem;
        }
    }

    private void ensureSize(int size){
        if(this.size > this.data.length){
            E[] newData = (E[]) new Object[this.data.length*GROW_FACTOR];
            for (int i = 0; i < this.size-1; i++){
                newData[i] = this.data[i];
            }
            this.data = newData;
        }
    }


    @Override
    public void addFront(E e) {
        this.size += 1;
        ensureSize(this.size);
        E[] newData = (E[]) new Object[this.data.length];
        for (int i = 1; i < this.size; i++){
            newData[i] = this.data[i-1];

        }
        newData[0] = e;
        this.data = newData;
    }

    @Override
    public void addBack(E e) {
        this.size += 1;
        ensureSize(this.size);
        this.data[this.size-1] = e;

    }

    @Override
    public E removeFront() {
        if (this.size != 0){
            E[] newData = (E[]) new Object[this.data.length];
            for (int i = 0; i < this.size - 1; i++) {
                newData[i] = this.data[i + 1];
            }
            E removed = this.data[0];
            this.data = newData;
            this.size -= 1;
            return removed;
        }
        return null;
    }

    @Override
    public E removeBack() {
        E removed = null;
        if (this.size != 0) {
            removed = this.data[this.size - 1];
            this.data[this.size - 1] = null;
            this.size -= 1;
        }
        return removed;
    }

    @Override
    public boolean enqueue(E e) {
        addFront(e);
        return true;
    }

    @Override
    public E dequeue() {
        E removed = removeBack();
        return removed;
    }

    @Override
    public boolean push(E e) {
        addBack(e);
        return true;
    }

    @Override
    public E pop() {
        return removeBack();
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        return this.data[0];
    }

    @Override
    public E peekBack() {
        if (this.size != 0) {
            return this.data[this.size - 1];
        }
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        Iterator<E> itr = new ADIterator();
        return itr;
    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString(){
        String ans = "[";
        for (E item: data){
            if (item != null) {
                ans = ans + item.toString() + ", ";
            }
        }
        if (this.size != 0) {
            //System.out.println(ans);
            ans = ans.substring(0, ans.length()-2);
        }
        ans = ans + ']';
        return ans;
    }
}
