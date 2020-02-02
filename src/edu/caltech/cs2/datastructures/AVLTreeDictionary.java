package edu.caltech.cs2.datastructures;

public class AVLTreeDictionary<K extends Comparable<K>, V> extends BSTDictionary<K, V> {

    private static class AVLNode<K, V> extends BSTNode<K, V> {

        public int height;

        public AVLNode(K key, V value, int height) {
            super(key, value);
            this.height = height;
        }

    }

    /**
     * Overrides the remove method in BST
     *
     * @param key
     * @return The value of the removed BSTNode if it exists, null otherwise
     */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }
}



