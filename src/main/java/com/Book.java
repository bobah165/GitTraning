package com;

import java.util.HashMap;
import java.util.Map;

public class Book {
    final Map<Integer, String> map = new HashMap<>();

    int add(String title) {
        Integer next = this.map.size() + 1;
        this.map.put(next, title);
        return next;
    }

    String title(int id) {
        return this.map.get(id);
    }
}
