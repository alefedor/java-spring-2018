package ru.spbau.fedorov.architectures.util;

import java.lang.ref.WeakReference;

public class GarbageCollectorRunner {
    public static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
        obj = null;
        while(ref.get() != null) {
            System.gc();
        }
    }
}
