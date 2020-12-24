package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.common;

import java.io.Serializable;

public class Pair<K,V> implements Serializable {

    private K key;

    public K getKey() { return key; }
    public K getFirst() { return key; }
    public K getLeft() { return key; }

    private V value;

    public V getValue() { return value; }
    public V getSecond() { return value; }
    public V getRight() { return value; }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

    @Override
    public int hashCode() {
        // name's hashCode is multiplied by an arbitrary prime number (13)
        // in order to make sure there is a difference in the hashCode between
        // these two parameters:
        //  name: a  value: aa
        //  name: aa value: a
        return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Pair) {
            Pair pair = (Pair) o;
            if (key != null ? !key.equals(pair.key) : pair.key != null) return false;
            if (value != null ? !value.equals(pair.value) : pair.value != null) return false;
            return true;
        }
        return false;
    }
}
