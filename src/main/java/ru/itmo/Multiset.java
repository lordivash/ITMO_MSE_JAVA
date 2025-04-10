package ru.itmo;

import java.util.*;

public class Multiset<E> extends AbstractCollection<E> {
	private LinkedHashMap<E, Integer> m = new LinkedHashMap<>();
	private int size = 0;

	/**
	 * Returns the number of occurrences of an element in this multiset.
	 * <p>
	 * Expected complexity: Same as `contains`
	 */
	public int count(E element) {
		return m.getOrDefault(element, 0);
	}

	@Override
	public boolean remove(Object o) {
		Integer count = m.get(o);
		if (count == 0) {
			return false;
		}
		if (count == 1) {
			m.remove(o);
		} else {
			m.put((E) o, count - 1);
		}

		--size;
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return m.containsKey(o);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean add(E element) {
		m.put(element, m.getOrDefault(element, 0) + 1);
		++size;

		return true;
	}

	/**
	 * Returns the set of distinct elements contained in this multiset.
	 * <p>
	 * Expected complexity: O(1)
	 */
	public Set<E> elementSet() {
		return m.keySet();
	}

	/**
	 * Returns the set of entries representing the data of this multiset.
	 * <p>
	 * Expected complexity: O(1)
	 */

	public Set<Entry<E>> entrySet() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Elements that occur multiple times in the multiset will appear multiple times in this iterator.
	 * <p>
	 * Expected complexity: O(1)
	 */
	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public static class Entry<E> {

		public E getElement() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public int getCount() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}
}
