package ru.finnetrolle.tools.cachingcontainer;

import java.util.function.Supplier;

/**
 * Created by finnetrolle on 24.11.2015.
 */

/**
 * Container to help you cache your objects
 * @param <T> - any type derives Object
 */
public class CachingContainer<T> {

    /**
     * Value for one second
     */
    public static final long SECONDS = 1000L;
    /**
     * Value for one minute
     */
    public static final long MINUTES = 60 * SECONDS;
    /**
     * Value for one hour
     */
    public static final long HOURS = 60 * MINUTES;
    /**
     * Value for one day
     */
    public static final long DAYS = 24 * HOURS;

    private final long livingTime;
    private volatile long updatedTimstamp = 0L;
    private T value;

    /**
     * Factory method to create caching object
     * @param livingTime time in millis you want value to live
     * @param <T> type of value
     * @return instance of caching container
     */
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

    /**
     * Method allows you to provide supplier method with checked exceptions
     * @param supplier supplier method
     * @return value
     * @throws Exception
     */
    public T getChecked(CheckedSupplier<T> supplier) throws Exception {
        return (isInitialized() && isUpToDate())
                ? value
                : update(supplier.get());
    }

    /**
     * Method checks time of last access and load value from supplier if TTL expires or from cached value
     * @param supplier supplier method
     * @return value
     */
    public T get(Supplier<T> supplier) {
        return (isInitialized() && isUpToDate())
                ? value
                : update(supplier.get());
    }

    /**
     * Override this method in your derived class if you want to check age of cached value
     * @return time in millis
     */
    protected long getAge() {
        return (isInitialized()) ? System.currentTimeMillis() - livingTime : 0L;
    }

    /**
     * Invalidates cache to lazy-loading
     */
    public void invalidate() {
        this.updatedTimstamp = 0L;
        value = null;
    }

    /**
     * Invalidates cache with setting new value
     * @param supplier new value providing method
     */
    public void invalidate(Supplier<T> supplier) {
        update(supplier.get());
    }

    /**
     * Invalidates cache with setting new value
     * Provider method can throw Exception
     * @param supplier new value providing method
     * @throws Exception
     */
    public void invalidateChecked(CheckedSupplier<T> supplier) throws Exception {
        update(supplier.get());
    }

}
