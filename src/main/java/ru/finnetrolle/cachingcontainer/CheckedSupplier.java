package ru.finnetrolle.cachingcontainer;


/**
 * Created by finnetrolle on 24.11.2015.
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    T get() throws Exception;
}
