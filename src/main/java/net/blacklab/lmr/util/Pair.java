package net.blacklab.lmr.util;


public interface Pair<K, V> {

    public K getKey();
    public Pair<K, V> setKey(K s);

    public V getValue();
    public Pair<K, V> setValue(V v);

}