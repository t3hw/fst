package dev.t3hw.fstest.common.avltree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NavigableMapTest {

    private AVLTreeMap<Integer, String> map;

    @BeforeEach
    void setUp() {
        map = new AVLTreeMap<>();
        map.put(10, "Ten");
        map.put(20, "Twenty");
        map.put(30, "Thirty");

        var set = map.entrySet();

        assertEquals(3, set.size());
    }

    @Test
    void testCeilingEntry() {
        Map.Entry<Integer, String> entry = map.ceilingEntry(15);
        assertNotNull(entry);
        assertEquals(20, entry.getKey());
        assertEquals("Twenty", entry.getValue());
    }

    @Test
    void testCeilingKey() {
        Integer key = map.ceilingKey(15);
        assertNotNull(key);
        assertEquals(20, key);
    }

    @Test
    void testFloorEntry() {
        Map.Entry<Integer, String> entry = map.floorEntry(25);
        assertNotNull(entry);
        assertEquals(20, entry.getKey());
        assertEquals("Twenty", entry.getValue());
    }

    @Test
    void testFloorKey() {
        Integer key = map.floorKey(25);
        assertNotNull(key);
        assertEquals(20, key);
    }

    @Test
    void testHigherEntry() {
        Map.Entry<Integer, String> entry = map.higherEntry(20);
        assertNotNull(entry);
        assertEquals(30, entry.getKey());
        assertEquals("Thirty", entry.getValue());
    }

    @Test
    void testHigherKey() {
        Integer key = map.higherKey(20);
        assertNotNull(key);
        assertEquals(30, key);
    }

    @Test
    void testLowerEntry() {
        Map.Entry<Integer, String> entry = map.lowerEntry(20);
        assertNotNull(entry);
        assertEquals(10, entry.getKey());
        assertEquals("Ten", entry.getValue());
    }

    @Test
    void testLowerKey() {
        Integer key = map.lowerKey(20);
        assertNotNull(key);
        assertEquals(10, key);
    }

    @Test
    void testFirstEntry() {
        Map.Entry<Integer, String> entry = map.firstEntry();
        assertNotNull(entry);
        assertEquals(10, entry.getKey());
        assertEquals("Ten", entry.getValue());
    }

    @Test
    void testLastEntry() {
        Map.Entry<Integer, String> entry = map.lastEntry();
        assertNotNull(entry);
        assertEquals(30, entry.getKey());
        assertEquals("Thirty", entry.getValue());
    }

    @Test
    void testPollFirstEntry() {
        Map.Entry<Integer, String> entry = map.pollFirstEntry();
        assertNotNull(entry);
        assertEquals(10, entry.getKey());
        assertEquals("Ten", entry.getValue());
        assertEquals(2, map.size());
    }

    @Test
    void testPollLastEntry() {
        Map.Entry<Integer, String> entry = map.pollLastEntry();
        assertNotNull(entry);
        assertEquals(30, entry.getKey());
        assertEquals("Thirty", entry.getValue());
        assertEquals(2, map.size());
    }
}