package ru.noties.listeners;

import android.support.annotation.NonNull;

/**
 * Simple data structure aimed at storing listeners or observers, that allow adding/removal of
 * elements whilst iterating without copying underlying collection. This comes at a cost: only one single
 * iteration can happen at a time (no nested ones). So, if your listener doesn\'t just listen but also modifies
 * state so another iteration/notification must happen, this collection won\'t do you any good.
 * Anyway for a simple collection of listeners that operate inside one thread this could be a good
 * solution.
 * <p>
 * Please note that as this data structure keeps track of iteration state, methods {@link #begin()} and {@link #end()}
 * must be explicitly called. However if you are planning on iterating on the whole collection calling
 * {@link #end()} is not required. But if you exit iteration early (with a `break` or somehow differently)
 * explicit call to end must take place.
 */
@SuppressWarnings("WeakerAccess")
public abstract class Listeners<T> {

    /**
     * Factory method to create an instance of {@link Listeners} with default capacity
     *
     * @return an instance of {@link Listeners}
     * @see #create(int)
     */
    @NonNull
    public static <T> Listeners<T> create() {
        return create(10);
    }

    /**
     * Factory method to obtain an instance of {@link Listeners} with specified capacity.
     *
     * @param initialCapacity initial capacity for underlying collection
     * @return an instance of {@link Listeners}
     */
    @SuppressWarnings("SameParameterValue")
    @NonNull
    public static <T> Listeners<T> create(int initialCapacity) {
        return new ListenersImpl<>(initialCapacity);
    }

    /**
     * Adds element to this collection (at the end). If iteration is currently happening this newly
     * added element will be included in it.
     *
     * @param t to add
     */
    public abstract void add(@NonNull T t);

    /**
     * Removes element from this collection. If iteration is currently happening this element (if not already
     * visited) won\'t be delivered to iteration. Currently active element in iteration can safely
     * remove self.
     *
     * @param t element to remove
     */
    public abstract void remove(@NonNull T t);

    /**
     * Clears underlying data structure
     */
    public abstract void clear();

    /**
     * @return size of underlying data structure
     */
    public abstract int size();

    /**
     * @return boolean flag indicating if we are currently iterating
     */
    public abstract boolean isIterating();

    /**
     * Starts iteration. {@code for (Element e: list.begin()) {}}. Please note, that if you exit
     * iteration early (with a `break` or some condition) an explicit call to {@link #end()} must follow.
     * <p>
     * Please note that returned Iterable is cached (so as Iterator), so each call to begin will return
     * the same instance
     *
     * @return Iterable to be used in for-in loop
     * @throws IllegalStateException if there is already another iteration
     * @see #end()
     */
    @NonNull
    public abstract Iterable<T> begin() throws IllegalStateException;

    /**
     * Marks as finished previous iteration or finishes current one if called whilst iterating
     */
    public abstract void end();
}
