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

@SuppressWarnings("unused")
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
    public void each_element_removes_first() {

        final List<Object> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(new Object());
        }

        final Listeners<Object> listeners = Listeners.create();
        for (Object o : list) {
            listeners.add(o);
        }

        int iterations = 0;

        // actually the same as removing self
        for (Object o : listeners.begin()) {
            listeners.remove(list.get(0));
            iterations += 1;
        }

        assertEquals(10, iterations);
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

    @SuppressWarnings("unused")
    @Test
    public void nested_begin_with_reversed() {

        final Listeners<Object> listeners = Listeners.create(5);
        listeners.add(new Object());

        boolean started = false;

        for (Object o1 : listeners.begin()) {
            started = true;
            try {
                //noinspection StatementWithEmptyBody
                for (Object o2 : listeners.beginReversed()) ;
                assertTrue(false);
            } catch (IllegalStateException e) {
                assertTrue(true);
            }
        }

        assertTrue(started);
    }

    @Test
    public void nested_begin_2_reversed() {

        final Listeners<Object> listeners = Listeners.create(7);
        listeners.add(new Object());

        boolean started = false;

        //noinspection unused
        for (Object o1 : listeners.beginReversed()) {
            started = true;
            try {
                listeners.beginReversed();
                assertTrue(false);
            } catch (IllegalStateException e) {
                assertTrue(true);
            }
        }
        assertTrue(started);
    }

    @Test
    public void each_element_removes_self_reversed() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 9; i++) {
            listeners.add(new Object());
        }

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            iterations += 1;
            listeners.remove(o);
        }

        assertEquals(9, iterations);
        assertEquals(0, listeners.size());
    }

    @Test
    public void each_element_removes_next_reversed() {

        final Listeners<Object> listeners = Listeners.create();

        final Object[] objects = new Object[10];

        for (int i = 0; i < 10; i++) {
            objects[i] = new Object();
            listeners.add(objects[i]);
        }

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            if (iterations != 0) {
                listeners.remove(objects[objects.length - iterations]);
            }
            iterations += 1;
        }

        assertEquals(10, iterations);
        assertEquals(1, listeners.size());
    }

    @Test
    public void each_element_removes_first_reversed() {

        final List<Object> list = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            list.add(new Object());
        }

        final Listeners<Object> listeners = Listeners.create();
        for (Object o : list) {
            listeners.add(o);
        }

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            if (iterations == 5) {
                assertEquals(o, list.get(0));
            }
            listeners.remove(list.remove(0));
            iterations += 1;
        }

        // actually 6 as last item removes self
        assertEquals(6, iterations);
    }

    @Test
    public void each_element_removes_previous_reversed() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        Object previous = null;
        int iterations = 0;

        for (Object o : listeners.beginReversed()) {

            if (previous != null) {
                listeners.remove(previous);
            }

            previous = o;
            iterations += 1;
        }

        assertEquals(10, iterations);
        assertEquals(1, listeners.size());
    }

    @Test
    public void natural_iteration_end_reversed() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        assertFalse(listeners.isIterating());

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            assertTrue(listeners.isIterating());
            iterations += 1;
        }

        assertFalse(listeners.isIterating());
        assertEquals(10, iterations);
    }

    @Test
    public void add_during_iteration_reversed() {
        // must not affect iteration (as we are adding at the end)

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            listeners.add(new Object());
            iterations += 1;
        }

        assertEquals(10, iterations);
        assertEquals(20, listeners.size());
    }

    @Test
    public void end_called_inside_iteration_reversed() {

        final Listeners<Object> listeners = Listeners.create();
        for (int i = 0; i < 10; i++) {
            listeners.add(new Object());
        }

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            if (iterations == 5) {
                listeners.end();
            } else {
                iterations += 1;
            }
        }

        assertEquals(5, iterations);
    }

    @Test
    public void iterator_remove_throws_reversed() {

        final Listeners<Object> listeners = Listeners.create();
        listeners.add(new Object());

        try {
            listeners.beginReversed().iterator().remove();
            assertTrue(false);
        } catch (UnsupportedOperationException e) {
            assertTrue(true);
        }
    }

    @Test
    public void no_iteration() {

        final Listeners<Object> listeners = Listeners.create();

        int iterations = 0;

        for (Object o : listeners.begin()) {
            iterations += 1;
        }

        assertEquals(0, iterations);
        assertFalse(listeners.isIterating());
    }

    @Test
    public void no_iteration_reversed() {

        final Listeners<Object> listeners = Listeners.create();

        int iterations = 0;

        for (Object o : listeners.beginReversed()) {
            iterations += 1;
        }

        assertEquals(0, iterations);
        assertFalse(listeners.isIterating());
    }
}
