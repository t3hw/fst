package dev.t3hw.fstest.common.avltree;

public class AVLTreeNode<K ,V> {
    
    public AVLTreeNode(K key, V value) {
        this.key = key;
        this.value = value;
        this.height = 1;
        this.left = null;
        this.right = null;
    }
    
    K key;
    V value;
    int height;
    AVLTreeNode<K,V> left;
    AVLTreeNode<K,V> right;
    
    @Override
    public String toString() {
        return "Node: {" +
               "key=" + key +
               ", value=" + value +
               ", height=" + height +
               '}';
    }
}
