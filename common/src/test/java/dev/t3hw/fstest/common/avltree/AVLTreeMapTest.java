package dev.t3hw.fstest.common.avltree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeMapTest {

    private AVLTreeMap<Integer, String> avlTree;

    @BeforeEach
    void setUp() {
        avlTree = new AVLTreeMap<>();
    }

    @Test
    void testPutAndGet() {
        avlTree.put(10, "Ten");
        avlTree.put(20, "Twenty");
        avlTree.put(5, "Five");

        assertEquals("Ten", avlTree.get(10));
        assertEquals("Twenty", avlTree.get(20));
        assertEquals("Five", avlTree.get(5));
    }

    @Test
    void testPutMaintainsBalance() {
        avlTree.put(10, "Ten");
        avlTree.put(20, "Twenty");
        avlTree.put(30, "Thirty"); // Right-Right case

        assertEquals(2, avlTree.getHeight()); // Tree height should be balanced
    }

    @Test
    void testRemove() {
        avlTree.put(10, "Ten");
        avlTree.put(20, "Twenty");
        avlTree.put(5, "Five");
        avlTree.put(0, "Zero");
        avlTree.put(3, "Three");
        avlTree.put(15, "Fifteen");
        avlTree.put(25, "Twenty-Five");
        avlTree.put(13, "Thirteen");
        avlTree.put(17, "Seventeen");


        avlTree.remove(20);
        assertNull(avlTree.get(20));
        assertEquals(4, avlTree.getHeight());

        avlTree.remove(15);
        assertNull(avlTree.get(15));
        assertEquals(3, avlTree.getHeight());
    }

    @Test
    void testRemoveMaintainsBalance() {
        avlTree.put(10, "Ten");
        avlTree.put(20, "Twenty");
        avlTree.put(5, "Five");
        avlTree.put(15, "Fifteen");

        avlTree.remove(20); // Removing a node with one child
        assertEquals(2, avlTree.getHeight()); // Tree should remain balanced
    }

    @Test
    void testContainsKey() {
        avlTree.put(10, "Ten");
        avlTree.put(20, "Twenty");

        assertTrue(avlTree.containsKey(10));
        assertFalse(avlTree.containsKey(5));
    }

    @Test
    void testIsEmpty() {
        assertTrue(avlTree.isEmpty());
        avlTree.put(10, "Ten");
        assertFalse(avlTree.isEmpty());
    }

    @Test
    void testGetHeight() {
        assertEquals(0, avlTree.getHeight()); // Empty tree
        avlTree.put(10, "Ten");
        avlTree.put(20, "Twenty");
        avlTree.put(5, "Five");
        assertEquals(2, avlTree.getHeight()); // Balanced tree
    }

    @Test
    void testNullKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> avlTree.put(null, "Null"));
        assertThrows(IllegalArgumentException.class, () -> avlTree.get(null));
        assertThrows(IllegalArgumentException.class, () -> avlTree.remove(null));
    }

    @Test
    void testDuplicateKeyWithoutOverwrite() {
        avlTree = new AVLTreeMap<>(1, null, false); // Overwrite not allowed
        avlTree.put(10, "Ten");

        assertThrows(AVLTreeMap.NodeAlreadyExistsException.class, () -> avlTree.put(10, "Duplicate"));
    }

    @Test
    void testDuplicateKeyWithOverwrite() {
        avlTree = new AVLTreeMap<>(1, null, true); // Overwrite allowed
        avlTree.put(10, "Ten");
        avlTree.put(10, "Updated");

        assertEquals("Updated", avlTree.get(10));
    }

    @Test
    void testCustomComparator() {
        AVLTreeMap<String, Integer> customTree = new AVLTreeMap<String,Integer>(1, Comparator.reverseOrder());
        customTree.put("b", 2);
        customTree.put("a", 1);
        customTree.put("c", 3);

        assertEquals(2, customTree.getHeight());
        assertEquals(2, customTree.get("b"));
    }
}