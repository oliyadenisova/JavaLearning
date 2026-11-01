package ru.oliya.learning.hashmap;

import java.util.Objects;

public class CustomHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<K, V>[] buckets;
    private int size;
    private final float loadFactor;

    public CustomHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }
    public CustomHashMap(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    // Основной конструктор
    @SuppressWarnings("unchecked")
    public CustomHashMap(int capacity, float loadFactor) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Capacity must be greater than zero"
            );
        }
        if (loadFactor <= 0 || !Float.isFinite(loadFactor)) {
            throw new IllegalArgumentException(
                    "Load factor must be a positive finite number"
            );
        }
        this.buckets = (Node<K, V>[]) new Node[capacity];
        this.loadFactor = loadFactor;
        this.size = 0;
    }

    public V put(K key, V value) {

        int index = getIndex(key);
        Node<K, V> current = buckets[index];

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }

        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
        size++;
        if (size > buckets.length * loadFactor) {
            resize();
        }
        return null;
    }

    public V get(K key) {
        int index = getIndex(key);
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public V remove(K key) {
        int index = getIndex(key);
        Node<K, V> current = buckets[index];
        Node<K, V> previous = null;
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        int index = getIndex(key);
        Node<K, V> current = buckets[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    private int getIndex(K key) {
        int hash = Objects.hashCode(key);
        return Math.floorMod(hash, buckets.length);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        buckets = (Node<K, V>[]) new Node[oldBuckets.length * 2];
        for (Node<K, V> head : oldBuckets) {
            Node<K, V> current = head;
            while (current != null) {
                Node<K, V> next = current.next;
                int newIndex = getIndex(current.key);
                current.next = buckets[newIndex];
                buckets[newIndex] = current;
                current = next;
            }
        }
    }

    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return size == 0;
    }
    public int capacity() {
        return buckets.length;
    }
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}