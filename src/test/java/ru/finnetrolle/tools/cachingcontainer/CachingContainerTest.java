package ru.finnetrolle.tools.cachingcontainer;

import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by finnetrolle on 24.11.2015.
 */
public class CachingContainerTest {

    private final static long TTL = 20L;

    @Test
    public void testGet() throws Exception {
        CachingContainer<String> container = CachingContainer.build(TTL);
        assertEquals(container.get(() -> "Hello"), "Hello");
        assertEquals(container.get(() -> "World"), "Hello");
        TimeUnit.MILLISECONDS.sleep(TTL * 2);
        assertEquals(container.get(() -> "World"), "World");
    }

    @Test(expected = Exception.class)
    public void testCheckedValue() throws Exception {
        CachingContainer<String> container = CachingContainer.build(TTL);
        container.getChecked(() -> {throw new Exception();});
    }

    @Test
    public void testInvalidate() {
        CachingContainer<String> container = CachingContainer.build(TTL);
        assertEquals("Hello", container.get(() -> "Hello"));
        assertEquals("Hello", container.get(() -> "World"));
        container.invalidate();
        assertEquals("World", container.get(() -> "World"));
    }

    @Test(expected = Exception.class)
    public void testInvalidateChecked() throws Exception {
        CachingContainer<String> container = CachingContainer.build(TTL);
        assertEquals("Hello", container.get(() -> "Hello"));
        container.invalidateChecked(() -> {throw new Exception();});
    }

    @Test
    public void testInvalidateEager() throws Exception {
        CachingContainer<String> container = CachingContainer.build(TTL);
        assertEquals("Hello", container.get(() -> "Hello"));
        assertEquals("Hello", container.get(() -> "World"));
        container.invalidate(() -> "Earth");
        assertEquals("Earth", container.get(() -> "Spoil"));
    }


}