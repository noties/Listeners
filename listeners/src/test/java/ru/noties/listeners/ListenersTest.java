package ru.noties.listeners;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ListenersTest {

    @SuppressWarnings("unused")
    @Test
    public void new_iteration_requested_whilst_previous_not_finished() {
        final Listeners<Object> listeners = Listeners.create();
        listeners.add(new Object());
        boolean started = false;
        for (Object o1 : listeners.begin()) {
            started = true;
            try {
                for (Object o2 : listeners.begin()) {
                    assertTrue(false);
                }
            } catch (IllegalStateException e) {
                assertTrue(true);
            }
        }
        assertTrue(started);
    }

    @Test
    public void correct_size() {
        final Listeners<Object> listeners = Listeners.create();
        assertEquals(0, listeners.size());
        for (int i = 1; i <= 13; i++) {
            listeners.add(new Object());
            assertEquals(i, listeners.size());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void clear_whilst_iterating() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }
        assertEquals(10, listeners.size());

        int i = 0;
        for (Object o : listeners.begin()) {
            i += 1;
            if (i == 5) {
                // after we have cleared, no new iteration should occur
                listeners.clear();
            }
        }
        assertEquals(5, i);
    }

    @Test
    public void each_element_removes_self() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        assertEquals(10, listeners.size());

        int iterations = 0;

        for (Object o : listeners.begin()) {
            iterations += 1;
            listeners.remove(o);
        }

        assertEquals(0, listeners.size());
        assertEquals(iterations, 10);
    }

    @SuppressWarnings("unused")
    @Test
    public void each_element_removes_next() {

        final Object[] objects = new Object[10];
        for (int i = 0; i < 10; i++) {
            objects[i] = new Object();
        }

        final Listeners<Object> listeners = Listeners.create();
        for (Object o : objects) {
            listeners.add(o);
        }

        int i = 0;
        for (Object o : listeners.begin()) {
            assertNotNull(objects[i++]);
            listeners.remove(objects[i]);
            objects[i++] = null;
        }

        for (int p = 0; p < 10; p++) {
            if (p % 2 == 0) {
                assertNotNull(objects[p]);
            } else {
                assertNull(objects[p]);
            }
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void each_element_removes_last() {

        final List<Object> objects = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            objects.add(new Object());
        }

        final Listeners<Object> listeners = Listeners.create();
        for (Object o : objects) {
            listeners.add(o);
        }

        int iterations = 0;
        for (Object o : listeners.begin()) {
            iterations += 1;
            listeners.remove(objects.remove(objects.size() - 1));
        }

        assertEquals(5, iterations);
        assertEquals(5, listeners.size());
        assertEquals(5, objects.size());
    }

    @Test
    public void each_element_removes_previous() {

        // does not affect iteration, all still must be iterated

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        int iterations = 0;
        Object previous = null;
        for (Object o : listeners.begin()) {
            if (previous != null) {
                listeners.remove(previous);
            }
            previous = o;
            iterations += 1;
        }

        assertEquals(10, iterations);
        assertEquals(1, listeners.size());
    }

    @SuppressWarnings("unused")
    @Test
    public void natural_iteration_end() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        assertFalse(listeners.isIterating());

        for (Object o : listeners.begin()) {
            assertTrue(listeners.isIterating());
        }

        assertFalse(listeners.isIterating());
    }

    @SuppressWarnings("unused")
    @Test
    public void early_iteration_exit_keeps_iteration_flag() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        assertFalse(listeners.isIterating());

        int iterations = 0;
        for (Object o : listeners.begin()) {
            iterations += 1;
            assertTrue(listeners.isIterating());
            if (iterations == 5) {
                break;
            }
        }

        assertTrue(listeners.isIterating());
    }

    @SuppressWarnings("unused")
    @Test
    public void add_during_iteration() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        assertEquals(10, listeners.size());

        int iterations = 0;

        for (Object o : listeners.begin()) {
            iterations += 1;

            // otherwise we would end up with stackOverFlow (ideally :) )
            if (iterations == 20) {
                break;
            }

            listeners.add(new Object());
        }

        assertEquals(20, iterations);
    }

    @SuppressWarnings("unused")
    @Test
    public void end_called_inside_iteration() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        assertEquals(10, listeners.size());

        int iterations = 0;

        for (Object o : listeners.begin()) {
            if (++iterations == 5) {
                listeners.end();
            }
        }

        assertEquals(5, iterations);
    }

    @Test
    public void iterator_remove_throws() {

        final Listeners<Object> listeners = Listeners.create();
        listeners.add(new Object());

        final Iterator<Object> iterator = listeners.begin().iterator();
        iterator.next();

        try {
            iterator.remove();
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void empty_iterator_throws() {

        final Listeners<Object> listeners = Listeners.create();

        try {
            listeners.begin().iterator().next();
            assertTrue(false);
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void iterator_access_after_iteration_finished() {

        final Listeners<Object> listeners = Listeners.create();
        listeners.add(new Object());
        listeners.add(new Object());

        final Iterator<Object> iterator = listeners.begin().iterator();

        listeners.end();

        //noinspection StatementWithEmptyBody
        for (Object o : listeners.begin()) {

        }

        try {
            iterator.next();
            assertTrue(false);
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }
}
