package net.formicary.utils.indexedCSV;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class AbstractCSVList<V> implements List<V> {
    public boolean isEmpty() {
        return size() > 0;
    }

    public boolean contains(Object o) {
        throw new NotImplementedException();
    }

    public Object[] toArray() {
        throw new NotImplementedException();
    }

    public <T> T[] toArray(T[] ts) {
        throw new NotImplementedException();
    }

    public boolean add(V v) {
        throw new NotImplementedException();
    }

    public boolean remove(Object o) {
        throw new NotImplementedException();
    }

    public boolean containsAll(Collection<?> objects) {
        throw new NotImplementedException();
    }

    public boolean addAll(Collection<? extends V> vs) {
        throw new NotImplementedException();
    }

    public boolean addAll(int i, Collection<? extends V> vs) {
        throw new NotImplementedException();
    }

    public boolean removeAll(Collection<?> objects) {
        throw new NotImplementedException();
    }

    public boolean retainAll(Collection<?> objects) {
        throw new NotImplementedException();
    }

    public void clear() {
        throw new NotImplementedException();
    }

    public V set(int i, V v) {
        throw new NotImplementedException();
    }

    public void add(int i, V v) {
        throw new NotImplementedException();
    }

    public V remove(int i) {
        throw new NotImplementedException();
    }

    public int indexOf(Object o) {
        throw new NotImplementedException();
    }

    public int lastIndexOf(Object o) {
        throw new NotImplementedException();
    }

    public ListIterator<V> listIterator() {
        throw new NotImplementedException();
    }

    public ListIterator<V> listIterator(int i) {
        throw new NotImplementedException();
    }

    public List<V> subList(int i, int i1) {
        throw new NotImplementedException();
    }
}
