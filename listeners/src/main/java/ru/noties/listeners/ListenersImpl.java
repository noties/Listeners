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

        @Override
        public boolean hasNext() {
            final boolean result = isIterating && index < size();
            if (!result
                    && isIterating) {
                end();
            }
            return result;
        }

        @Override
        public T next() {

            if (!isIterating
                    || index < 0
                    || index >= size()) {
                throw new NoSuchElementException();
            }

            return list.get(index++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
