package com.github.dhirabayashi.mycat.util;

/**
 * ラムダ式から参照できるカウンター
 */
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
