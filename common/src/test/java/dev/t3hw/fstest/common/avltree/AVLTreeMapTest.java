package dev.t3hw.fstest.common.avltree;

import org.junit.jupiter.api.Test;

import dev.t3hw.fstest.common.avltree.AVLTreeMap.OverrideStrategy;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NavigableMap;

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
    void sortedByKey() {
        NavigableMap<String, String> stringTree = new AVLTreeMap<>();
        stringTree.put("plant", "Plant");
        stringTree.put("plant/fruit/banana", "Banana");
        stringTree.put("plant/fruit/apple", "Apple");
        stringTree.put("plant/vegetable/cabbage", "Cabbage");
        stringTree.put("plant/fruit/cherry", "Cherry");
        stringTree.put("plant/vegetable/carrot", "Carrot");
        stringTree.put("plant/fruit", "Fruit");
        stringTree.put("plant/fruit/date", "Date");
        stringTree.put("plant/vegetable/broccoli", "Broccoli");
        stringTree.put("plant/vegetable", "Vegetable");
        stringTree.put("plant/fruit/fig", "Fig");
        stringTree.put("plant/vegetable/eggplant", "Eggplant");
        stringTree.put("animal", "Animal");
        stringTree.put("animal/mammal/dog", "Dog");
        stringTree.put("animal/bird", "Bird");
        stringTree.put("animal/mammal/cat", "Cat");
        stringTree.put("animal/bird/eagle", "Eagle");
        stringTree.put("animal/mammal", "Mammal");
        stringTree.put("animal/reptile/snake", "Snake");
        stringTree.put("animal/mammal/elephant", "Elephant");
        stringTree.put("animal/bird/sparrow", "Sparrow");
        stringTree.put("animal/bird/parrot", "Parrot");
        stringTree.put("animal/reptile", "Reptile");
        stringTree.put("animal/reptile/lizard", "Lizard");

        assertEquals("Banana", stringTree.get("plant/fruit/banana"));

        System.out.println();
        System.out.println(stringTree.lowerKey("plant/fruit/apple"));
        System.out.println(stringTree.floorKey("plant/fruit/apple"));

        System.out.println();
        System.out.println();

        stringTree.forEach(
            (key, value) -> {
                System.out.println(key + ": " + value);
            }
        );
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
        avlTree = new AVLTreeMap<Integer,String>(1, OverrideStrategy.NONE); // Overwrite not allowed
        avlTree.put(10, "Ten");

        assertThrows(AVLTreeMap.NodeAlreadyExistsException.class, () -> avlTree.put(10, "Duplicate"));
    }

    @Test
    void testDuplicateKeyWithOverwrite() {
        avlTree = new AVLTreeMap<Integer,String>(1, OverrideStrategy.OVERWRITE); // Overwrite allowed
        avlTree.put(10, "Ten");
        avlTree.put(10, "Updated");

        assertEquals("Updated", avlTree.get(10));
    }

}