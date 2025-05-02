package dev.t3hw.fstest.common.avltree;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class AVLTreeMap<K extends Comparable<K> ,V> extends AbstractMap<K,V> { //  implements NavigableMap<K,V> {
    private final int AVL_BALANCE_THRESHOLD;
    private final boolean ALLOW_OVERWRITE;
    private final Comparator<K> DEFAULT_COMPARATOR;
    private final Map<String, Comparator<V>> VALUES_COMPARATORS;

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
        this(avlBalanceThreshold, null);
    }
    /**
     * Constructor for the AVLTreeMap
     * @param avlBalanceThreshold The balance factor to use for the tree
     * @param comparator The comparator to use for the keys
     */
    public AVLTreeMap(int avlBalanceThreshold, Comparator<K> comparator) {
        this(avlBalanceThreshold, comparator, false);
    }
    /**
     * Constructor for the AVLTreeMap
     * @param avlBalanceThreshold The balance factor to use for the tree
     * @param comparator The comparator to use for the keys
     * @param allowOverwrite Whether to allow overwriting existing keys
     */
    public AVLTreeMap(int avlBalanceThreshold, Comparator<K> comparator, boolean allowOverwrite) {
        this(avlBalanceThreshold, comparator, allowOverwrite, null);
    }
    /**
     * Constructor for the AVLTreeMap
     * @param avlBalanceThreshold The balance factor to use for the tree
     * @param comparator The comparator to use for the keys
     * @param allowOverwrite Whether to allow overwriting existing keys
     * @param valuesComparators The secondary comparators to use for the values
     */
    public AVLTreeMap(int avlBalanceThreshold, Comparator<K> comparator, boolean allowOverwrite, Map<String, Comparator<V>> valuesComparators) {
        this.AVL_BALANCE_THRESHOLD = avlBalanceThreshold;
        this.DEFAULT_COMPARATOR = comparator;
        this.ALLOW_OVERWRITE = allowOverwrite;
        this.VALUES_COMPARATORS = valuesComparators;
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
    public V get(K key) {
        if (key == null) {
           throw new IllegalArgumentException("Key cannot be null");
       }
       AVLTreeNode<K, V> node = find(root, key);
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
    public void remove(K key) {
        if (key == null) {
           throw new IllegalArgumentException("Key cannot be null");
        }
        var result = delete(root, key);

        // If the node was found and deleted, decrement the size
        if (result != null) {
            this.size--;
        }
    }

    /**
     * Checks if the map contains a mapping for the specified key.
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     * @throws IllegalArgumentException if the key is null
     */
    public boolean containsKey(K key) {
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

    private record ModificationResult<K extends Comparable<K>, V>(AVLTreeNode<K,V> root, V target) {
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

        final int compareResult = key.compareTo(node.key);
        
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
            if (!ALLOW_OVERWRITE) {
                throw new NodeAlreadyExistsException("Key already exists: " + key);
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

            final int compareResult = key.compareTo(current.key);
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
            final int compareResult = key.compareTo(current.key);
            
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

        // TODO: Insert dynamic node accumulating calculations here
    }

    // Method overrides from the AbstractMap class

    @Override
    public Set<Entry<K, V>> entrySet() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'entrySet'");
    }


    public static class NodeAlreadyExistsException extends RuntimeException {
        public NodeAlreadyExistsException(String message) {
            super(message);
        }
    }
}


//     @Override
//     public Comparator<? super K> comparator() {
//         return DEFAULT_COMPARATOR;
//     }

//     @Override
//     public K firstKey() {
//         var current = root;
//         while (current.left != null) {
//             current = current.left;
//         }
//         return current.key;
//     }
//     @Override
//     public K lastKey() {
//         var current = root;
//         while (current.right != null) {
//             current = current.right;
//         }
//         return current.key;
//     }
//     @Override
//     public Set<Entry<K, V>> entrySet() {
//         var entries = new TreeMap<K,V>();
//         inOrderTraversal(root, entries);
//     }
//     @Override
//     public Entry<K, V> lowerEntry(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'lowerEntry'");
//     }
//     @Override
//     public K lowerKey(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'lowerKey'");
//     }
//     @Override
//     public Entry<K, V> floorEntry(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'floorEntry'");
//     }
//     @Override
//     public K floorKey(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'floorKey'");
//     }
//     @Override
//     public Entry<K, V> ceilingEntry(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'ceilingEntry'");
//     }
//     @Override
//     public K ceilingKey(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'ceilingKey'");
//     }
//     @Override
//     public Entry<K, V> higherEntry(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'higherEntry'");
//     }
//     @Override
//     public K higherKey(K key) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'higherKey'");
//     }
//     @Override
//     public Entry<K, V> firstEntry() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'firstEntry'");
//     }
//     @Override
//     public Entry<K, V> lastEntry() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'lastEntry'");
//     }
//     @Override
//     public Entry<K, V> pollFirstEntry() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'pollFirstEntry'");
//     }
//     @Override
//     public Entry<K, V> pollLastEntry() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'pollLastEntry'");
//     }
//     @Override
//     public NavigableMap<K, V> descendingMap() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'descendingMap'");
//     }
//     @Override
//     public NavigableSet<K> navigableKeySet() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'navigableKeySet'");
//     }
//     @Override
//     public NavigableSet<K> descendingKeySet() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'descendingKeySet'");
//     }
//     @Override
//     public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'subMap'");
//     }
//     @Override
//     public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'headMap'");
//     }
//     @Override
//     public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'tailMap'");
//     }
//     @Override
//     public SortedMap<K, V> subMap(K fromKey, K toKey) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'subMap'");
//     }
//     @Override
//     public SortedMap<K, V> headMap(K toKey) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'headMap'");
//     }
//     @Override
//     public SortedMap<K, V> tailMap(K fromKey) {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'tailMap'");
//     }
// }
   