package edu.caltech.cs2.datastructures;

import edu.caltech.cs2.interfaces.IDeque;

import java.util.Iterator;

public class LinkedDeque<E> implements IDeque<E> {
    private Node<E> head;
    private int size;
    private Node<E> tail;

    private class Node<E>{
        private E data;
        private Node<E> next;
        private Node<E> previous;

        public Node(E data, Node next, Node previous){
            this.data = data;
            this.next = next;
            this.previous = previous;
        }
    }

    private class LDIterator implements Iterator<E>{
        private int currentIndex = 0;
        private Node<E> nextNode = LinkedDeque.this.head;

        public boolean hasNext(){
            return currentIndex < LinkedDeque.this.size();
        }

        public E next(){
            E result = nextNode.data;
            this.currentIndex++;
            nextNode = nextNode.next;
            return result;
        }
    }

    public LinkedDeque(){
        head = null;
        tail = null;
        size = 0;

    }

    @Override
    public void addFront(E e) {
        if (this.head == null){
            this.head = new Node<E>(e,null, null);
            this.tail = this.head;
        }
        else {
            Node<E> newHead = new Node(e, this.head, null);
            this.head.previous = newHead;
            this.head = newHead;
        }
        this.size ++;
    }

    @Override
    public void addBack(E e) {
        Node<E> current = this.head;
        if (this.head == null){
            this.head = new Node<E>(e,null, null);
            this.tail = this.head;
        }
        else{
            Node<E> newTail = new Node<E>(e, null, this.tail);
            this.tail.next = newTail;
            this.tail = newTail;
        }
        this.size++;
    }

    @Override
    public E removeFront() {
        E removed = null;
        if (this.head != null){
            removed = this.head.data;
            /*
            if (this.head.next != null) {
                this.head.next.previous = null;
            }
            */
            this.head = this.head.next;
            this.size--;
            if (this.size == 1){
                this.tail = this.head;
            }
        }
        return removed;
    }

    @Override
    public E removeBack() {
        E removed = null;
        if (this.head != null && this.tail != null ){
            removed = this.tail.data;

            this.tail = this.tail.previous;
            if (this.tail == null){
                this.head = null;
            }
            if (this.tail != null){
                this.tail.next = null;
            }
            this.size--;
        }
        return removed;
    }

    @Override
    public E peek() {
        return peekBack();
    }

    @Override
    public E peekFront() {
        if (this.size != 0) {
            return this.head.data;
        }
        return null;
    }

    @Override
    public E peekBack() {
        if (this.size != 0 && this.tail != null) {
            return this.tail.data;
        }
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return new LDIterator();
    }

    @Override
    public int size() {
        return this.size;
    }

    public String toString(){
        String ans = "[";
        Node<E> current = this.head;
        while( current != null){
            ans = ans + current.data + ", ";
            current = current.next;
        }
        if (this.size != 0){
            ans = ans.substring(0, ans.length()-2);
        }
        ans = ans + "]";
        return ans;
    }
}
