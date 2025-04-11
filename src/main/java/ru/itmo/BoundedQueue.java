package ru.itmo;

import java.util.AbstractCollection;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Iterator;

public class BoundedQueue<T> extends AbstractCollection<T> implements Queue<T> {
    private int first = 0;
    private int last = 0;
    private int capacity = 0;
    private int size = 0;
    private final Object[] buf;
    private boolean containsLast = false;

    BoundedQueue(int capacity) {
        buf = new Object[capacity];
        this.capacity = capacity;
    }

    @Override
    public boolean offer(T t) {
        if (containsLast && (last + 1) % capacity != first) {
            last = (last + 1) % capacity;
            containsLast = false;
        }

        buf[last] = t;

        if (size < capacity) {
            ++size;
        }

        if ((last + 1) % capacity != first) {
            last = (last + 1) % capacity;
        } else {
            containsLast = true;
        }

        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T remove() {
        if (size > 0) {
            T head = (T) buf[first];
            first = (first + 1) % capacity;
            --size;
            return head;
        }

        throw new NoSuchElementException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T poll() {
        if (size > 0) {
            T head = (T) buf[first];
            first = (first + 1) % capacity;
            --size;
            return head;
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T element() {
        if (size > 0) {
            return (T) buf[first];
        }

        throw new NoSuchElementException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T peek() {
        if (size > 0) {
            return (T) buf[first];
        }

        return null;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new QueueIterator();
    }

    private void remove(int pos) {
        int prev = pos;
        for (int i = (pos + 1) % capacity; i != (last + 1) % capacity; i = (i + 1) % capacity) {
            buf[prev] = buf[i];
            prev = i;
        }

        --last;
        if (last < 0) last += capacity;
        --size;
    }

    private class QueueIterator implements Iterator<T> {
        int cur;
        int lastRet = -1;
        int steps = 0;

        QueueIterator() {
            cur = BoundedQueue.this.first;
        }

        @Override
        public boolean hasNext() {
            return steps < BoundedQueue.this.size;
        }

        @Override
        @SuppressWarnings("unchecked")
        public T next() {
            T element = (T) BoundedQueue.this.buf[cur];
            lastRet = cur;
            cur = (cur + 1) % BoundedQueue.this.capacity;
            ++steps;
            return element;
        }

        @Override
        public void remove() {
            if (lastRet < 0) {
                throw new IllegalStateException();
            }

            // Check for CME here

            BoundedQueue.this.remove(lastRet);
            cur = lastRet;
            lastRet = -1;
            --steps;
        }
    }
}