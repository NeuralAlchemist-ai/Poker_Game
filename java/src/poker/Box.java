package com.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Box<T extends Comparable<T>> {
    private List<T> items;
    
  
    public Box() {
        this.items = new ArrayList<>();
    }
    
    public void add(T item) {
        if (item != null) {
            items.add(item);
        }
    }
    
    public boolean remove(T item) {
        return items.remove(item);
    }
    
    public T get(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return null;
    }
    
    public int size() {
        return items.size();
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public void clear() {
        items.clear();
    }
    
    public List<T> getAll() {
        return new ArrayList<>(items);
    }
    
    public void sort() {
        Collections.sort(items);
    }

    public T getMax() {
        if (items.isEmpty()) return null;
        return Collections.max(items);
    }
    
    public T getMin() {
        if (items.isEmpty()) return null;
        return Collections.min(items);
    }
    
    @Override
    public String toString() {
        return items.toString();
    }
}