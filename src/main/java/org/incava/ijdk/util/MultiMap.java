package org.incava.ijdk.util;

import java.util.*;


public class MultiMap<K, V> extends AbstractMap<K, Collection<V>> {

    private Set<Map.Entry<K, Collection<V>>> entrySet;

    public MultiMap() {
        entrySet = null;
    }

    // get entrySet on access; allow to override

    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        if (entrySet == null) {
            entrySet = new HashSet<Map.Entry<K, Collection<V>>>();
        }

        return entrySet;
    }

    public Collection<V> getCollection() {
        return new ArrayList<V>();
    }

    public Collection<V> put(K key, Collection<V> value) {
        Set<Map.Entry<K, Collection<V>>> entries = entrySet();

        for (Map.Entry<K, Collection<V>> entry : entries) {
            if (key == entry.getKey() || (key != null && key.equals(entry.getKey()))) {
                return entry.setValue(value);
            }
        }

        entries.add(new MultiMapEntry<K, V>(key, value));

        return null;
    }

    public Collection<V> put(K key, V ... values) {
        Collection<V> coll = get(key);

        if (coll == null) {
            coll = getCollection();
            put(key, coll);
        }

        for (V val : values) {
            coll.add(val);
        }
    
        return coll;
    }

    public Collection<V> put(K key, V value) {
        Collection<V> coll = get(key);

        if (coll == null) {
            coll = getCollection();
            put(key, coll);
        }

        coll.add(value);
    
        return coll;
    }

    public class MultiMapEntry<K, V> implements Map.Entry<K, Collection<V>>, Comparable<Map.Entry<K, Collection<V>>> {
        
        private final K key;

        private Collection<V> value;

        public MultiMapEntry(K key, Collection<V> value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public Collection<V> getValue() {
            return value;
        }

        public Collection<V> setValue(Collection<V> newValue) {
            Collection<V> oldValue = value;
            value = newValue;
            return oldValue;
        }

        public boolean equals(Object obj) {
            if (obj instanceof MultiMapEntry) {
                MultiMapEntry other = (MultiMapEntry)obj;
                return areEqual(key, other.getKey()) && areEqual(value, other.getValue());
            }
            else {
                return false;
            }
        }

        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
        }

        public int compareTo(Map.Entry<K, Collection<V>> other) {
            int cmp = compareObjects(key, other.getKey());
            if (cmp == 0) {
                cmp = compareObjects(value, other.getValue());
            }
            return cmp;
        }

        public boolean areEqual(Object first, Object second) {
            return first == null ? second == null : first.equals(second);
        }

        @SuppressWarnings("unchecked")
        public int compareObjects(Object first, Object second) {
            if (first == second) { // NOPMD
                return 0;
            }
            else if (first == null) {
                return 1;
            }
            else if (second == null) {
                return -1;
            }
            else if (first instanceof Comparable && second instanceof Comparable) {
                return ((Comparable)first).compareTo((Comparable)second);
            }
            else {
                return 1;
            }
        }
    }
}
