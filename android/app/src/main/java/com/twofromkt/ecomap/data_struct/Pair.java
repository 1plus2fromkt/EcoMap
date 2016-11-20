package com.twofromkt.ecomap.data_struct;

import java.io.Serializable;

public class Pair<T1, T2> implements Serializable {
    public T1 val1;
    public T2 val2;

    public Pair(T1 val1, T2 val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public boolean equals(Object o) {
        return o instanceof Pair && equals((Pair) o);
    }

    public boolean equals(Pair t) {
        return val1.equals(t.val1) && val2.equals(t.val2);
    }

    public int hashCode() {
        return val1.hashCode() * 52367 + val2.hashCode() * 75247;
    }

    public String toString() {
        return "[" + val1.toString() + " ; " + val2.toString() + "]";
    }
}