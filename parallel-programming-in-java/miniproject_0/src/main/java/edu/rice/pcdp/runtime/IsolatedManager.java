//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.rice.pcdp.runtime;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class IsolatedManager {
    private final int nLocks = 64;
    private final Lock[] locks = new Lock[64];

    public IsolatedManager() {
        for(int i = 0; i < this.locks.length; ++i) {
            this.locks[i] = new ReentrantLock();
        }

    }

    private int lockIndexFor(Object obj) {
        return Math.abs(obj.hashCode()) % 64;
    }

    private TreeSet<Object> createSortedObjects(Object[] objects) {
        TreeSet<Object> sorted = new TreeSet(new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                return IsolatedManager.this.lockIndexFor(o1) - IsolatedManager.this.lockIndexFor(o2);
            }
        });
        Object[] var3 = objects;
        int var4 = objects.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Object obj = var3[var5];
            sorted.add(obj);
        }

        return sorted;
    }

    public void acquireAllLocks() {
        for(int i = 0; i < this.locks.length; ++i) {
            this.locks[i].lock();
        }

    }

    public void releaseAllLocks() {
        for(int i = this.locks.length - 1; i >= 0; --i) {
            this.locks[i].unlock();
        }

    }

    public void acquireLocksFor(Object[] objects) {
        TreeSet<Object> sorted = this.createSortedObjects(objects);
        Iterator var3 = sorted.iterator();

        while(var3.hasNext()) {
            Object obj = var3.next();
            int lockIndex = this.lockIndexFor(obj);
            this.locks[lockIndex].lock();
        }

    }

    public void releaseLocksFor(Object[] objects) {
        TreeSet<Object> sorted = this.createSortedObjects(objects);
        Iterator var3 = sorted.iterator();

        while(var3.hasNext()) {
            Object obj = var3.next();
            int lockIndex = this.lockIndexFor(obj);
            this.locks[lockIndex].unlock();
        }

    }
}
