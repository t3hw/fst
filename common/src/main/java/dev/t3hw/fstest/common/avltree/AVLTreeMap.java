package dev.t3hw.fstest.common.avltree;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.function.BiConsumer;

public class AVLTreeMap<K ,V> implements NavigableMap<K,V> {

    public enum OverrideStrategy {
        NONE,
        OVERWRITE,
    }

    @SuppressWarnings("unchecked")
    private final Comparator<K> DEFAULT_COMPARATOR = (Comparator<K>) Comparator.naturalOrder();
    private final int AVL_BALANCE_THRESHOLD;
    private final OverrideStrategy OVERWRITE_STRATEGY;
    private final List<BiConsumer<K, V>> VALUES_OPERATOR;

    private AVLTreeNode<K,V> root = null;

    private volatile int size = 0;
    
    /**
     * Default Constructor for the AVLTreeMap
     */
    public AVLTreeMap() {
        this(1);
    }
    /**
     * Constructor for the AVLTreeMap
     * @param avlBalanceThreshold The balance factor to use for the tree
     */
    public AVLTreeMap(int avlBalanceThreshold) {
        this(avlBalanceThreshold, OverrideStrategy.NONE);
    }
    /**
     * Constructor for the AVLTreeMap
     * @param avlBalanceThreshold The balance factor to use for the tree
     * @param allowOverwrite Whether to allow overwriting existing keys
     */
    public AVLTreeMap(int avlBalanceThreshold, OverrideStrategy overwriteStrategy) {
        this(avlBalanceThreshold, overwriteStrategy, List.of());
    }
    /**
     * Constructor for the AVLTreeMap
     * @param avlBalanceThreshold The balance factor to use for the tree
     * @param allowOverwrite Whether to allow overwriting existing keys
     * @param valuesOperator The secondary comparators to use for the values
     */
    public AVLTreeMap(int avlBalanceThreshold, OverrideStrategy overwriteStrategy, List<BiConsumer<K, V>> valuesOperator) {
        this.AVL_BALANCE_THRESHOLD = avlBalanceThreshold;
        this.OVERWRITE_STRATEGY = overwriteStrategy;
        this.VALUES_OPERATOR = valuesOperator;
    }

    // --- Public Map Methods ---


    /**
     * Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if none
     * @throws IllegalArgumentException if the key is null
     */
    public V get(Object key) {
        if (key == null) {
           throw new IllegalArgumentException("Key cannot be null");
       }
       @SuppressWarnings("unchecked")
       AVLTreeNode<K, V> node = find(root, (K) key);
       return (node == null) ? null : node.value;
   }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws IllegalArgumentException if the key is null
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        var result = insert(root, key, value);
        root = result.root;
        if (result.target != null) {
            return result.target;
        } else {
            this.size++;
            return null;
        }
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @throws IllegalArgumentException if the key is null
     */
    public V remove(Object key) {
        if (key == null) {
           throw new IllegalArgumentException("Key cannot be null");
        }
        @SuppressWarnings("unchecked")
        var result = delete(root, (K) key);

        // If the node was found and deleted, decrement the size
        if (result != null) {
            this.size--;
        }

        return result;
    }

    /**
     * Checks if the map contains a mapping for the specified key.
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     * @throws IllegalArgumentException if the key is null
     */
    public boolean containsKey(Object key) {
        return get(key) != null;
    } 
 
    /**
     * Returns the height of the tree. Returns 0 for an empty tree.
     * @return the height of the tree
     */
    public int getHeight() {
        return getHeight(root);
    }

    /**
    * Checks if the tree is empty.
    * @return true if the tree has no nodes, false otherwise.
    */
    public boolean isEmpty() {
        return root == null;
    }


    // --- Private Helper Methods ---

    private record ModificationResult<K, V>(AVLTreeNode<K,V> root, V target) {
        /**
         * Constructor for the initial node
         * @param key The key of the node
         * @param value The value of the node
         */
        public ModificationResult(AVLTreeNode<K,V> root) {
            this(root, null);
        }
        /**
         * Constructor for the recursive node
         * @param root The root of the tree
         * @param target The previous node
         */
        public ModificationResult(AVLTreeNode<K,V> root, V target) {
            this.root = root;
            this.target = target;
        }
    }

    private ModificationResult<K,V> insert(AVLTreeNode<K,V> node, K key, V value) {
        V target = null;
        
        if (node == null) {
            return new ModificationResult<>(new AVLTreeNode<>(key, value));
        }

        // final int compareResult = key.compareTo(node.key);
        final int compareResult = DEFAULT_COMPARATOR.compare(key, node.key);
        
        ModificationResult <K,V> result = null;

        // Traverse the tree to find the correct position for the new node
        if (compareResult != 0) {
            if (compareResult < 0) {
                result = insert(node.left, key, value);
                node.left = result.root;
            } else if (compareResult > 0) {
                result = insert(node.right, key, value);
                node.right = result.root;
            }

            target = result.target;

        // Node with the key already exists
        } else {
            if (OVERWRITE_STRATEGY == OverrideStrategy.NONE) {

                switch (OVERWRITE_STRATEGY) {
                    case NONE:
                        throw new NodeAlreadyExistsException("Key already exists: " + key);
                    case OVERWRITE:
                    default:
                }
            }

            // Overwrite the value
            target = node.value;
            node.value = value;
            
            // Recalculate node data dynamic calculations
            updateCurrentNode(node);

            return new ModificationResult<>(node, target);            
        }
        
        // Update the height of the current node
        updateCurrentNode(node);

        // Check the balance factor
        return new ModificationResult<>(balance(node), target);
    }

    /**
     * Recursive helper function to delete a node with a given key.
     * Performs AVL balancing after deletion.
     */
    private V delete(AVLTreeNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        V target = null;

        Stack<AVLTreeNode<K, V>> path = new Stack<>();
        Stack<Boolean> isLeftChild = new Stack<>();

        AVLTreeNode<K, V> current = node;
        AVLTreeNode<K, V> parent = null;
        boolean found = false;

        // Lookup stage
        while (current != null) {
            if (current.key.equals(key)) {
                found = true;
                target = current.value;
                break;
            }

            parent = current;
            path.push(parent);

            // final int compareResult = key.compareTo(current.key);
            final int compareResult = DEFAULT_COMPARATOR.compare(key, current.key);
            if (compareResult < 0) {
                current = current.left;
                isLeftChild.push(true);
            } else if (compareResult > 0) {
                current = current.right;
                isLeftChild.push(false);
            }
        }

        if (!found) {
            return null;
        }

        // Deletion stage

        // Case 1: Node has no children or one child
        if (current.left == null || current.right == null) {
            AVLTreeNode<K,V> child = (current.left != null) ? current.left : current.right;

            if (parent == null) {
                // Deleting the root
                root = child;
            } else {
                if (isLeftChild.peek()) {
                    parent.left = child;
                } else {
                    parent.right = child;
                }
            }
        }
        // Case 2: Node has two children
        else {
            var successor = findMin(current.right);
                        
            // Store the path to successor for rebalancing
            var successorParent = current;
            var temp = current.right;

            while (temp != null && temp != successor) {
                successorParent = temp;
                path.push(successorParent);
                isLeftChild.push(temp.left != null && temp.left.key == successor.key);
                temp = temp.left;
            }

            // Copy successor data to current node
            current.key = successor.key;
            current.value = successor.value;

            // Delete successor
            if (successorParent == current) {
                successorParent.right = successor.right;
            } else {
                successorParent.left = successor.right;
            }
        }

        // Rebalance the tree
        rebalance(path, isLeftChild);

        return target;
    }


    /**
     * Finds the node with the specified key in the tree.
     * @param node The current node to start searching from
     * @param key The key to search for
     * @return The node with the specified key, or null if not found
     */
    private AVLTreeNode<K,V> find(AVLTreeNode<K,V> node, K key) {
        AVLTreeNode<K,V> current = node;
        
        while (current != null) {
            // final int compareResult = key.compareTo(current.key);
            final int compareResult = DEFAULT_COMPARATOR.compare(key, current.key);
            
            if (compareResult < 0) {
                current = current.left;
            } else if (compareResult > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        
        return null;
    }

    /**
     * Finds the node with the minimum key in the subtree rooted at node.
     * @param isLeftChild 
     * @param path 
     */
    private AVLTreeNode<K, V> findMin(AVLTreeNode<K, V> node) {
        AVLTreeNode<K, V> current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }
    
    private AVLTreeNode<K, V> findMax(AVLTreeNode<K, V> node) {
        AVLTreeNode<K, V> current = node;
        while (current.right != null) {
            current = current.right;
        }
        return current;
    }

    
    // --- AVL Balancing Methods ---

    /** Rebalances the subtree rooted at the given node if necessary. */
    private AVLTreeNode<K,V> balance(AVLTreeNode<K, V> node) {
        int balanceFactor = getBalanceFactor(node);

        // Left Heavy case
        if (balanceFactor > AVL_BALANCE_THRESHOLD) {
            // Left-Right Case
            if (getBalanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            // Left-Left Case (Line)
            return rotateRight(node);
        }

        // Right Heavy case
        if (balanceFactor < -1*AVL_BALANCE_THRESHOLD) {
            // Right-Left Case
            if (getBalanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            // Right-Right Case
            return rotateLeft(node);
        }

        return node;
    }

    private void rebalance(Stack<AVLTreeNode<K,V>> path, Stack<Boolean> isLeftChild) {
        
        while (!path.isEmpty()) {
            AVLTreeNode<K, V> node = path.pop();
            @SuppressWarnings("unused")
            boolean wasLeftChild = isLeftChild.pop();
            
            updateCurrentNode(node);

            final int balanceFactor = getBalanceFactor(node);

            // Left Heavy case
            if (balanceFactor > AVL_BALANCE_THRESHOLD) {
                // Left-Right Case
                if (getBalanceFactor(node.left) < 0) {
                    node.left = rotateLeft(node.left);
                }
                // Left-Left Case
                if (path.isEmpty()) {
                    root = rotateRight(node);
                } else {
                    var parent = path.peek();
                    if (isLeftChild.peek()) {
                        parent.left = rotateRight(node);
                    } else {
                        parent.right = rotateRight(node);
                    }
                }
            // Right Heavy case
            } if (balanceFactor < -1*AVL_BALANCE_THRESHOLD) {
                // Right-Left Case
                if (getBalanceFactor(node.right) > 0) {
                    node.right = rotateRight(node.right);
                }
                // Right-Right Case
                if (path.isEmpty()) {
                    root = rotateLeft(node);
                } else {
                    var parent = path.peek();
                    if (isLeftChild.peek()) {
                        parent.left = rotateLeft(node);
                    } else {
                        parent.right = rotateLeft(node);
                    }
                }
            }
        }
    }

    /** 
     * Core rotation methods for AVL balancing.
     * Base assumption: 
     * x > y
     * prevXLeft and prevYRight are in between x and y, and can be shifted around as needed
     **/

    /** Performs a left rotation on the subtree rooted with x. */
    private AVLTreeNode<K, V> rotateLeft(AVLTreeNode<K, V> y) {
        AVLTreeNode<K,V> x = y.right;
        AVLTreeNode<K,V> prevXLeft = x.left;

        //  Y < prevXLeft < X
        // which means prevXLeft can be the right child of Y
        x.left = y;
        y.right = prevXLeft;

        // Update heights (order matters: update children first)
        updateCurrentNode(y);
        updateCurrentNode(x);

        // Return new root of the rotated subtree
        return x;
    }

    /** Performs a right rotation on the subtree rooted with y. */
    private AVLTreeNode<K, V> rotateRight(AVLTreeNode<K, V> x) {
        AVLTreeNode<K,V> y = x.left;
        AVLTreeNode<K,V> prevYRight = y.right;

        //  Y < prevYRight < X
        // which means prevYRight can be the left child of X
        y.right = x;
        x.left = prevYRight;

        // Update heights (order matters: update children first)
        updateCurrentNode(x);
        updateCurrentNode(y);

        // Return new root of the rotated subtree
        return y;
    }

    
    /** Calculates the balance factor of a node. */
    private int getBalanceFactor(AVLTreeNode<K, V> node) {
        return (node == null) ? 0 : getHeight(node.left) - getHeight(node.right);
    }
       
    /** Returns the height of a node (handles null nodes). */
    private int getHeight(AVLTreeNode<K, V> node) {
        return (node == null) ? 0 : node.height;
    }

    /** Updates the height of a node based on its children's heights. */
    private void updateHeight(AVLTreeNode<K, V> node) {
        if (node != null) {
            node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }

    private void updateCurrentNode(AVLTreeNode<K, V> node) {
        updateHeight(node);

        VALUES_OPERATOR.forEach(op -> {
            op.accept(node.key, node.value);
        });
    }
        
    public static class NodeAlreadyExistsException extends RuntimeException {
        public NodeAlreadyExistsException(String message) {
            super(message);
        }
    }

    // Method overrides from the AbstractMap class
    public int size() {
        return size;
    }

    // @Override
    // public Comparator<? super K> comparator() {
    //     return DEFAULT_COMPARATOR;
    // }
    // @Override
    public K firstKey() {
        return findMin(root).key;
    }
    // @Override
    public K lastKey() {
        return findMax(root).key;
    }
    // @Override
    public Entry<K, V> firstEntry() {
        var entry = findMin(root);
        if (entry == null) {
            return null;
        }
        return new AbstractMap.SimpleEntry<>(entry.key, entry.value);
    }
    // @Override
    public Entry<K, V> lastEntry() {
        var entry = findMax(root);
        if (entry == null) {
            return null;
        }
        return new AbstractMap.SimpleEntry<>(entry.key, entry.value);
    }
    // @Override
    public Entry<K, V> pollFirstEntry() {
        var entry = findMin(root);
        if (entry == null) {
            return null;
        }
        var result = new AbstractMap.SimpleEntry<>(entry.key, entry.value);
        remove(entry.key);
        return result;
    }
    // @Override
    public Entry<K, V> pollLastEntry() {
        var entry = findMax(root);
        if (entry == null) {
            return null;
        }
        var result = new AbstractMap.SimpleEntry<>(entry.key, entry.value);
        remove(entry.key);
        return result;
        
    }
    // @Override
    public Entry<K, V> lowerEntry(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        AVLTreeNode<K, V> result = null;
        AVLTreeNode<K, V> current = root;
        
        while (current != null) {
            // int cmp = current.key.compareTo(key);
            int cmp = DEFAULT_COMPARATOR.compare(current.key, key);

            
            if (cmp < 0) {
                // Current key is less than target key - potential candidate
                result = current;
                current = current.right;
            } else {
                // Current key is greater or equal - go left
                current = current.left;
            }
        }
        
        return (result == null) ? null : new AbstractMap.SimpleEntry<>(result.key, result.value);
    }

    // @Override
    public K lowerKey(K key) {
        Entry<K, V> entry = lowerEntry(key);
        return (entry == null) ? null : entry.getKey();
    }

    // @Override
    public Entry<K, V> floorEntry(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        AVLTreeNode<K, V> result = null;
        AVLTreeNode<K, V> current = root;
        
        while (current != null) {
            // int cmp = current.key.compareTo(key);
            int cmp = DEFAULT_COMPARATOR.compare(current.key, key);
            
            if (cmp == 0) {
                // Exact match
                return new AbstractMap.SimpleEntry<>(current.key, current.value);
            } else if (cmp < 0) {
                // Current key is less than target key - potential candidate
                result = current;
                current = current.right;
            } else {
                // Current key is greater - go left
                current = current.left;
            }
        }
        
        return (result == null) ? null : new AbstractMap.SimpleEntry<>(result.key, result.value);
    }

    public K floorKey(K key) {
        Entry<K, V> entry = floorEntry(key);
        return (entry == null) ? null : entry.getKey();
    }

    public Entry<K, V> ceilingEntry(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        AVLTreeNode<K, V> result = null;
        AVLTreeNode<K, V> current = root;
        
        while (current != null) {
            // int cmp = current.key.compareTo(key);
            int cmp = DEFAULT_COMPARATOR.compare(current.key, key);
            
            if (cmp == 0) {
                // Exact match
                return new AbstractMap.SimpleEntry<>(current.key, current.value);
            } else if (cmp > 0) {
                // Current key is greater than target key - potential candidate
                result = current;
                current = current.left;
            } else {
                // Current key is less - go right
                current = current.right;
            }
        }
        
        return (result == null) ? null : new AbstractMap.SimpleEntry<>(result.key, result.value);
    }

    public K ceilingKey(K key) {
        Entry<K, V> entry = ceilingEntry(key);
        return (entry == null) ? null : entry.getKey();
    }

    public Entry<K, V> higherEntry(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        
        AVLTreeNode<K, V> result = null;
        AVLTreeNode<K, V> current = root;
        
        while (current != null) {
            // int cmp = current.key.compareTo(key);
            int cmp = DEFAULT_COMPARATOR.compare(current.key, key);
            
            if (cmp > 0) {
                // Current key is greater than target key - potential candidate
                result = current;
                current = current.left;
            } else {
                // Current key is less or equal - go right
                current = current.right;
            }
        }
        
        return (result == null) ? null : new AbstractMap.SimpleEntry<>(result.key, result.value);
    }

    public K higherKey(K key) {
        Entry<K, V> entry = higherEntry(key);
        return (entry == null) ? null : entry.getKey();
    }

    @Override
    public NavigableMap<K, V> descendingMap() {
        return new DescendingSubMap<>(this, true, null, true, true, null, true);
    }

    @Override
    public NavigableSet<K> navigableKeySet() {
        return new AVLTreeSet<>(this);
    }

    @Override
    public NavigableSet<K> descendingKeySet() {
        return descendingMap().navigableKeySet();
    }

    @Override
    public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
        if (fromKey == null || toKey == null) {
            throw new IllegalArgumentException("Range keys cannot be null");
        }
        if (DEFAULT_COMPARATOR.compare(fromKey, toKey) > 0) {
            throw new IllegalArgumentException("fromKey > toKey");
        }
        return new SubMap<>(this, false, fromKey, fromInclusive, false, toKey, toInclusive);
    }

    @Override
    public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
        if (toKey == null) {
            throw new IllegalArgumentException("Range key cannot be null");
        }
        return new SubMap<>(this, true, null, true, false, toKey, inclusive);
    }

    @Override
    public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
        if (fromKey == null) {
            throw new IllegalArgumentException("Range key cannot be null");
        }
        return new SubMap<>(this, false, fromKey, inclusive, true, null, true);
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return subMap(fromKey, true, toKey, false);
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return headMap(toKey, false);
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return tailMap(fromKey, true);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    // Inner class needed for the entry set implementation
    private class EntrySet extends AbstractSet<Entry<K, V>> {
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public int size() {
            return AVLTreeMap.this.size();
        }
        
        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Entry<K, V> entry = (Entry<K, V>) o;
            V value = AVLTreeMap.this.get(entry.getKey());
            return value != null && value.equals(entry.getValue());
        }
        
        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            Entry<K, V> entry = (Entry<K, V>) o;
            return AVLTreeMap.this.remove(entry.getKey()) != null;
        }
        
        @Override
        public void clear() {
            AVLTreeMap.this.clear();
        }
    }

    // Iterator for the entry set
    private class EntryIterator implements Iterator<Entry<K, V>> {
        private Stack<AVLTreeNode<K, V>> stack = new Stack<>();
        private AVLTreeNode<K, V> current = root;
        
        public EntryIterator() {
            // Initialize to leftmost node
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
        }
        
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }
        
        @Override
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            
            AVLTreeNode<K, V> node = stack.pop();
            Entry<K, V> result = new AbstractMap.SimpleEntry<>(node.key, node.value);
            
            // If right child exists, push all left children of right child
            if (node.right != null) {
                current = node.right;
                while (current != null) {
                    stack.push(current);
                    current = current.left;
                }
            }
            
            return result;
        }
    }

    // Helper class for key set implementation
    private static class AVLTreeSet<E> extends AbstractSet<E> implements NavigableSet<E> {
        private final NavigableMap<E, ?> map;
        
        AVLTreeSet(NavigableMap<E, ?> map) {
            this.map = map;
        }
        
        @Override
        public Iterator<E> iterator() {
            return map.keySet().iterator();
        }
        
        @Override
        public int size() {
            return map.size();
        }
        
        @Override
        public boolean contains(Object o) {
            return map.containsKey(o);
        }
        
        @Override
        public boolean remove(Object o) {
            return map.remove(o) != null;
        }
        
        @Override
        public void clear() {
            map.clear();
        }
        
        @Override
        public Comparator<? super E> comparator() {
            return map.comparator();
        }
        
        @Override
        public E first() {
            return map.firstKey();
        }
        
        @Override
        public E last() {
            return map.lastKey();
        }
        
        @Override
        public E lower(E e) {
            return map.lowerKey(e);
        }
        
        @Override
        public E floor(E e) {
            return map.floorKey(e);
        }
        
        @Override
        public E ceiling(E e) {
            return map.ceilingKey(e);
        }
        
        @Override
        public E higher(E e) {
            return map.higherKey(e);
        }
        
        @Override
        public E pollFirst() {
            Map.Entry<E, ?> entry = map.pollFirstEntry();
            return (entry == null) ? null : entry.getKey();
        }
        
        @Override
        public E pollLast() {
            Map.Entry<E, ?> entry = map.pollLastEntry();
            return (entry == null) ? null : entry.getKey();
        }
        
        @Override
        public NavigableSet<E> descendingSet() {
            return new AVLTreeSet<>(map.descendingMap());
        }
        
        @Override
        public Iterator<E> descendingIterator() {
            return descendingSet().iterator();
        }
        
        @Override
        public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
            return new AVLTreeSet<>(map.subMap(fromElement, fromInclusive, toElement, toInclusive));
        }
        
        @Override
        public NavigableSet<E> headSet(E toElement, boolean inclusive) {
            return new AVLTreeSet<>(map.headMap(toElement, inclusive));
        }
        
        @Override
        public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
            return new AVLTreeSet<>(map.tailMap(fromElement, inclusive));
        }
        
        @Override
        public SortedSet<E> subSet(E fromElement, E toElement) {
            return subSet(fromElement, true, toElement, false);
        }
        
        @Override
        public SortedSet<E> headSet(E toElement) {
            return headSet(toElement, false);
        }
        
        @Override
        public SortedSet<E> tailSet(E fromElement) {
            return tailSet(fromElement, true);
        }
    }

    // Abstract class for SubMap implementations
    private abstract static class AbstractSubMap<K, V> extends AVLTreeMap<K, V> {
        final AVLTreeMap<K, V> m;
        
        AbstractSubMap(AVLTreeMap<K, V> m) {
            this.m = m;
        }
        
        // Abstract methods to be implemented by subclasses
        abstract boolean inRange(K key);
        abstract boolean inClosedRange(K key);
        
        // Common SubMap methods
        
        @SuppressWarnings("unchecked")
        @Override
        public boolean containsKey(Object key) {
            return inRange((K) key) && m.containsKey(key);
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public V get(Object key) {
            return !inRange((K) key) ? null : m.get(key);
        }
        
        @Override
        public V put(K key, V value) {
            if (!inRange(key)) {
                throw new IllegalArgumentException("Key out of range");
            }
            return m.put(key, value);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            return m.comparator();
        }
    }

    // Implementation for a standard SubMap
    private static class SubMap<K, V> extends AbstractSubMap<K, V> {
        final K from;
        final boolean fromStart;
        final boolean fromInclusive;
        final K to;
        final boolean toEnd; 
        final boolean toInclusive;
        
        SubMap(AVLTreeMap<K, V> m,
            boolean fromStart, K from, boolean fromInclusive,
            boolean toEnd, K to, boolean toInclusive) {
            super(m);
            this.from = from;
            this.fromStart = fromStart;
            this.fromInclusive = fromInclusive;
            this.to = to;
            this.toEnd = toEnd;
            this.toInclusive = toInclusive;
        }
        
        boolean inRange(K key) {
            return (fromStart || inFromRange(key)) && (toEnd || inToRange(key));
        }
        
        boolean inClosedRange(K key) {
            return (fromStart || inFromClosedRange(key)) && (toEnd || inToClosedRange(key));
        }
        
        boolean inFromRange(K key) {
            int cmp = m.comparator().compare(key, from);
            return fromInclusive ? cmp >= 0 : cmp > 0;
        }
        
        boolean inToRange(K key) {
            int cmp = m.comparator().compare(key, to);
            return toInclusive ? cmp <= 0 : cmp < 0;
        }
        
        boolean inFromClosedRange(K key) {
            return m.comparator().compare(key, from) >= 0;
        }
        
        boolean inToClosedRange(K key) {
            return m.comparator().compare(key, to) <= 0;
        }
        
        // NavigableMap implementation methods
        @Override
        public Set<Entry<K, V>> entrySet() {
            // Implementation similar to EntrySet but respecting range
            return new SubMapEntrySet();
        }
        
        @Override
        public K firstKey() {
            K key = fromStart ? m.firstKey() : 
                    fromInclusive ? m.ceilingKey(from) : m.higherKey(from);
            if (key == null || !inRange(key)) {
                throw new NoSuchElementException();
            }
            return key;
        }
        
        @Override
        public K lastKey() {
            K key = toEnd ? m.lastKey() : 
                    toInclusive ? m.floorKey(to) : m.lowerKey(to);
            if (key == null || !inRange(key)) {
                throw new NoSuchElementException();
            }
            return key;
        }
        
        // Additional SubMap methods...
        
        private class SubMapEntrySet extends AbstractSet<Entry<K, V>> {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new SubMapEntryIterator();
            }
            
            @Override
            public int size() {
                int count = 0;
                for (Iterator<Entry<K, V>> i = iterator(); i.hasNext(); i.next()) {
                    count++;
                }
                return count;
            }
        }
        
        private class SubMapEntryIterator implements Iterator<Entry<K, V>> {
            // private Entry<K, V> lastReturned = null;
            private Entry<K, V> next;
            
            SubMapEntryIterator() {
                if (fromStart) {
                    next = m.firstEntry();
                } else {
                    next = fromInclusive ? m.ceilingEntry(from) : m.higherEntry(from);
                }
                if (next != null && !inRange(next.getKey())) {
                    next = null;
                }
            }
            
            @Override
            public boolean hasNext() {
                return next != null;
            }
            
            @Override
            public Entry<K, V> next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                Entry<K, V> e = next;
                
                // Find next entry
                K key = e.getKey();
                next = m.higherEntry(key);
                if (next != null && !inRange(next.getKey())) {
                    next = null;
                }
                
                // lastReturned = e;
                return e;
            }
        }
    }

    // Implementation for descending map view
    private static class DescendingSubMap<K, V> extends SubMap<K, V> {
        DescendingSubMap(AVLTreeMap<K, V> m,
                        boolean fromStart, K from, boolean fromInclusive,
                        boolean toEnd, K to, boolean toInclusive) {
            super(m, fromStart, from, fromInclusive, toEnd, to, toInclusive);
        }
        
        @Override
        public Comparator<? super K> comparator() {
            Comparator<? super K> cmp = m.comparator();
            if (cmp == null) {
                return Collections.reverseOrder();
            } else {
                return Collections.reverseOrder(cmp);
            }
        }
        
        @Override
        public NavigableMap<K, V> descendingMap() {
            return new SubMap<>(m, fromStart, from, fromInclusive, toEnd, to, toInclusive);
        }
        
        // Override navigational methods to reverse their behavior
        @Override
        public Entry<K, V> lowerEntry(K key) {
            return inRange(key) ? m.higherEntry(key) : null;
        }
        
        @Override
        public K lowerKey(K key) {
            return inRange(key) ? m.higherKey(key) : null;
        }
        
        @Override
        public Entry<K, V> floorEntry(K key) {
            return inRange(key) ? m.ceilingEntry(key) : null;
        }
        
        @Override
        public K floorKey(K key) {
            return inRange(key) ? m.ceilingKey(key) : null;
        }
        
        @Override
        public Entry<K, V> ceilingEntry(K key) {
            return inRange(key) ? m.floorEntry(key) : null;
        }
        
        @Override
        public K ceilingKey(K key) {
            return inRange(key) ? m.floorKey(key) : null;
        }
        
        @Override
        public Entry<K, V> higherEntry(K key) {
            return inRange(key) ? m.lowerEntry(key) : null;
        }
        
        @Override
        public K higherKey(K key) {
            return inRange(key) ? m.lowerKey(key) : null;
        }
        
        @Override
        public K firstKey() {
            return super.lastKey();
        }
        
        @Override
        public K lastKey() {
            return super.firstKey();
        }
        
        @Override
        public Entry<K, V> firstEntry() {
            return super.lastEntry();
        }
        
        @Override
        public Entry<K, V> lastEntry() {
            return super.firstEntry();
        }
        
        @Override
        public Entry<K, V> pollFirstEntry() {
            return super.pollLastEntry();
        }
        
        @Override
        public Entry<K, V> pollLastEntry() {
            return super.pollFirstEntry();
        }
    }

    @Override
    public Set<K> keySet() {
        var set = new AVLTreeSet<K>(this);
        return set;
    }
    @Override
    public Collection<V> values() {
        var collection = new ArrayList<V>(size);
        var iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            collection.add(entry.getValue());
        }
        return collection;
    }
    @Override
    public boolean containsValue(Object value) {
        var iterator = entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            if (entry.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (var entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public Comparator<? super K> comparator() {
        return DEFAULT_COMPARATOR;
    }
}