class Unchanged {    
    public Collection<V> put(K key, V value) {
        Collection<V> coll = get(key);

        if (coll == null) {
            coll = getCollection();
            put(key, coll);
        }

        coll.add(value);
    
        return coll;
    }        

    public Collection<V> put(K key, V ... values) {
        Collection<V> coll = null;
        for (V val : values) {
            coll = put(key, val);
        }
    
        return coll;
    }
}
