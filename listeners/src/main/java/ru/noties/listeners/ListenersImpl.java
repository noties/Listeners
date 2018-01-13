package ru.noties.listeners;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class ListenersImpl<T> extends Listeners<T> implements Iterable<T> {

    private final List<T> list;

    private final IteratorImpl iterator;

    private boolean isIterating;

    ListenersImpl(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
        this.iterator = new IteratorImpl();
    }

    @Override
    public void add(@NonNull T t) {

        // we do not need to do anything even if iteration is happening
        // if we are iterating normally - this newly added element will be just
        //      added and presented via Iterator
        //
        // if we are iterating backwards - this newly added element will be just
        //      at the end and won't be presented via iteration

        list.add(t);
    }

    @Override
    public void remove(@NonNull T t) {

        if (isIterating) {

            final int index = list.indexOf(t);

            if (index > -1) {

                list.remove(index);

                // shift iterator position
                final int iteratorIndex = iterator.index;
                if (index < iteratorIndex) {
                    // just decrement the iterator index
                    iterator.index = iteratorIndex - 1;
                }
            }
        } else {
            list.remove(t);
        }
    }

    @Override
    public void clear() {
        list.clear();
        isIterating = false;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isIterating() {
        return isIterating;
    }

    @NonNull
    @Override
    public Iterable<T> begin() throws IllegalStateException {

        if (isIterating) {
            throw new IllegalStateException();
        }

        isIterating = true;

        iterator.index = 0;
        iterator.reversed = false;

        return this;
    }

    @NonNull
    @Override
    public Iterable<T> beginReversed() throws IllegalStateException {

        if (isIterating) {
            throw new IllegalStateException();
        }

        isIterating = true;

        iterator.index = size() - 1;
        iterator.reversed = true;

        return this;
    }

    @Override
    public void end() {
        isIterating = false;
    }

    @Override
    @NonNull
    public Iterator<T> iterator() {
        return iterator;
    }

    private class IteratorImpl implements Iterator<T> {

        int index;
        boolean reversed;

        @Override
        public boolean hasNext() {

            // early return if we are not in iteration state
            if (!isIterating) {
                return false;
            }

            final boolean result;

            if (!reversed) {
                result = index < size();
            } else {
                result = index > -1;
            }

            // finish _natural_ iteration
            if (!result) {
                end();
            }

            return result;
        }

        @Override
        public T next() {

            // this check should occur no matter how we iterate (normal|reversed)
            if (!isIterating
                    || index < 0
                    || index >= size()) {
                throw new NoSuchElementException();
            }

            final T next;

            if (!reversed) {
                next = list.get(index++);
            } else {
                next = list.get(index--);
            }

            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
