package com.github.dhirabayashi.mycat.util;

public class Counter {
    private int n;

    public Counter(int n) {
        this.n = n;
    }

    public void increment() {
        n++;
    }

    public int intValue() {
        return n;
    }
}
