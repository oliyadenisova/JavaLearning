package ru.oliya.learning.hashmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashMapTest {

    private CustomHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new CustomHashMap<>();
    }

    // 1. Проверка создания пустой таблицы
    @Test
    void shouldCreateEmptyHashMap() {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertEquals(16, map.capacity());
    }

    // 2. Проверка добавления элемента
    @Test
    void shouldAddElement() {
        map.put("Apple", 100);
        assertEquals(100, map.get("Apple"));
        assertEquals(1, map.size());
    }

    // 3. Проверка добавления нескольких элементов
    @Test
    void shouldAddMultipleElements() {
        map.put("Apple", 100);
        map.put("Orange", 200);
        map.put("Banana", 300);
        assertEquals(3, map.size());
        assertEquals(100, map.get("Apple"));
        assertEquals(200, map.get("Orange"));
        assertEquals(300, map.get("Banana"));
    }

    // 4. Проверка обновления существующего элемента
    @Test
    void shouldUpdateExistingValue() {
        map.put("Apple", 100);
        Integer oldValue = map.put("Apple", 500);
        assertEquals(100, oldValue);
        assertEquals(500, map.get("Apple"));
        assertEquals(1, map.size());
    }

    // 5. Проверка получения несуществующего элемента
    @Test
    void shouldReturnNullForMissingKey() {
        assertNull(map.get("Unknown"));
    }

    // 6. Проверка удаления существующего элемента
    @Test
    void shouldRemoveExistingElement() {
        map.put("Apple", 100);
        Integer removedValue = map.remove("Apple");
        assertEquals(100, removedValue);
        assertFalse(map.containsKey("Apple"));
        assertEquals(0, map.size());
    }

    // 7. Проверка удаления несуществующего элемента
    @Test
    void shouldNotChangeMapWhenRemovingMissingKey() {
        map.put("Apple", 100);
        Integer removedValue = map.remove("Unknown");
        assertNull(removedValue);
        assertEquals(1, map.size());
        assertEquals(100, map.get("Apple"));
    }

    // 8. Проверка коллизий
    @Test
    void shouldHandleCollisions() {
        CustomHashMap<CollisionKey, String> collisionMap =
                new CustomHashMap<>();

        CollisionKey key1 = new CollisionKey(1);
        CollisionKey key2 = new CollisionKey(2);
        CollisionKey key3 = new CollisionKey(3);
        collisionMap.put(key1, "One");
        collisionMap.put(key2, "Two");
        collisionMap.put(key3, "Three");
        assertEquals("One", collisionMap.get(key1));
        assertEquals("Two", collisionMap.get(key2));
        assertEquals("Three", collisionMap.get(key3));
        assertEquals(3, collisionMap.size());
    }

    // 9. Проверка удаления элемента из цепочки коллизий
    @Test
    void shouldRemoveElementFromCollisionChain() {

        CustomHashMap<CollisionKey, String> collisionMap =
                new CustomHashMap<>();
        CollisionKey key1 = new CollisionKey(1);
        CollisionKey key2 = new CollisionKey(2);
        CollisionKey key3 = new CollisionKey(3);
        collisionMap.put(key1, "One");
        collisionMap.put(key2, "Two");
        collisionMap.put(key3, "Three");
        collisionMap.remove(key2);
        assertEquals("One", collisionMap.get(key1));
        assertNull(collisionMap.get(key2));
        assertEquals("Three", collisionMap.get(key3));
        assertEquals(2, collisionMap.size());
    }

    // 10. Проверка увеличения массива
    @Test
    void shouldResizeWhenLoadFactorExceeded() {
        CustomHashMap<Integer, String> smallMap =
                new CustomHashMap<>(2, 0.75f);
        smallMap.put(1, "One");
        smallMap.put(2, "Two");
        assertEquals(4, smallMap.capacity());
        assertEquals(2, smallMap.size());

        assertEquals("One", smallMap.get(1));
        assertEquals("Two", smallMap.get(2));
    }

    // 11. Проверка перераспределения элементов после resize
    @Test
    void shouldKeepAllElementsAfterResize() {
        CustomHashMap<Integer, String> smallMap =
                new CustomHashMap<>(2, 0.75f);
        for (int i = 0; i < 10; i++) {
            smallMap.put(i, "Value" + i);
        }
        for (int i = 0; i < 10; i++) {
            assertEquals("Value" + i, smallMap.get(i));
        }
        assertEquals(10, smallMap.size());
    }

    // 12. Проверка обновления без изменения размера
    @Test
    void shouldNotIncreaseSizeWhenUpdatingValue() {
        map.put("Apple", 100);
        map.put("Apple", 200);
        map.put("Apple", 300);
        assertEquals(1, map.size());
        assertEquals(300, map.get("Apple"));
    }

    // 13. Проверка работы с null-ключом
    @Test
    void shouldSupportNullKey() {
        map.put(null, 100);
        assertEquals(100, map.get(null));
        assertTrue(map.containsKey(null));
        map.remove(null);
        assertFalse(map.containsKey(null));
    }

    // 14. Проверка работы с null-значением
    @Test
    void shouldSupportNullValue() {
        map.put("Apple", null);
        assertTrue(map.containsKey("Apple"));
        assertNull(map.get("Apple"));
        assertEquals(1, map.size());
    }

    // 15. Проверка некорректной ёмкости
    @Test
    void shouldThrowExceptionForInvalidCapacity() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new CustomHashMap<>(0)
        );
    }

    // Вспомогательный класс для создания коллизий
    private static class CollisionKey {

        private final int id;
        CollisionKey(int id) {
            this.id = id;
        }
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof CollisionKey)) {
                return false;
            }
            CollisionKey other = (CollisionKey) obj;
            return id == other.id;
        }
    }
}
