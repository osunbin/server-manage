package com.bin.sm.internal.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for a sequence of primitive integers.
 */
public class IntIterator implements Iterator<Integer> {
    private final int missingValue;
    private final int[] values;

    private int position;

    /**
     * Construct an {@link Iterator} over an array of primitives ints.
     *
     * @param missingValue to indicate the value is missing, i.e. not present or null.
     * @param values       to iterate over.
     */
    public IntIterator(final int missingValue, final int[] values) {
        this.missingValue = missingValue;
        this.values = values;
        this.position = -1;
    }

    public boolean hasNext() {
        final int[] values = this.values;
        while (position < values.length) {
            if (position >= 0 && values[position] != missingValue) {
                return true;
            }
            position++;
        }
        return false;
    }

    public Integer next() {
        return nextValue();
    }

    public void remove() {
        throw new UnsupportedOperationException("remove");
    }

    /**
     * Strongly typed alternative of {@link Iterator#next()} to avoid boxing.
     *
     * @return the next int value.
     */
    public int nextValue() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        final int value = values[position];
        position++;
        return value;
    }

    void reset() {
        position = 0;
    }
}