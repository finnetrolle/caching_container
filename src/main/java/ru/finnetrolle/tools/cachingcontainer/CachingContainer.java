package ru.finnetrolle.tools.cachingcontainer;

import java.util.function.Supplier;

/**
 * Created by finnetrolle on 24.11.2015.
 */
public class CachingContainer<T> {

    public static final long SECONDS = 1000L;
    public static final long MINUTES = 60 * SECONDS;
    public static final long HOURS = 60 * MINUTES;
    public static final long DAYS = 24 * HOURS;

    private final long livingTime;
    private volatile long updatedTimstamp = 0L;
    private T value;

    public static <T> CachingContainer<T> build(long livingTime) {
        return new CachingContainer<>(livingTime);
    }

    private CachingContainer(long livingTime) {
        this.livingTime = livingTime;
    }

    private boolean isInitialized() {
        return updatedTimstamp != 0L;
    }

    private boolean isUpToDate() {
        return livingTime + updatedTimstamp > System.currentTimeMillis();
    }

    private T update(T value) {
        this.updatedTimstamp = System.currentTimeMillis();
        this.value = value;
        return value;
    }

    public T getChecked(CheckedSupplier<T> supplier) throws Exception {
        return (isInitialized() && isUpToDate())
                ? value
                : update(supplier.get());
    }

    public T get(Supplier<T> supplier) {
        return (isInitialized() && isUpToDate())
                ? value
                : update(supplier.get());
    }

    protected long getAge() {
        return (isInitialized()) ? System.currentTimeMillis() - livingTime : 0L;
    }

}
