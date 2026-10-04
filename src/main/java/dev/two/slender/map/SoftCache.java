package dev.two.slender.map;

import java.lang.ref.SoftReference;
import java.util.function.Supplier;

/** Valor caro de leer que se guarda mientras haya memoria: si el GC lo suelta, se vuelve a leer al pedirlo. */
public final class SoftCache<T> {
    private final Supplier<T> loader;
    private SoftReference<T> ref = new SoftReference<>(null);

    public SoftCache(Supplier<T> loader) {
        this.loader = loader;
    }

    public synchronized T get() {
        T v = this.ref.get();
        if (v == null) {
            v = this.loader.get();
            this.ref = new SoftReference<>(v);
        }
        return v;
    }

    /** Como si el GC lo hubiera soltado (para tests). */
    synchronized void forget() {
        this.ref.clear();
    }
}
