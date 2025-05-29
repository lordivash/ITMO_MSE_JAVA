package ru.itmo;

import java.util.*;

public class ArraySet<E extends Comparable<? super E>> extends AbstractSet<E> implements NavigableSet<E> {
	private ArrayList<E> elements;
	private Comparator<? super E> comparator;

	public ArraySet() {
		this(Comparator.naturalOrder(), Collections.emptyList());
	}

	public ArraySet(Comparator<? super E> comparator) {
		this(comparator, Collections.emptyList());
	}

	public ArraySet(Collection<E> collection) {
		this(Comparator.naturalOrder(), collection);
	}

	public ArraySet(Collection<E> collection, Comparator<? super E> comparator) {
		this(comparator, collection);
	}

	private ArraySet(Comparator<? super E> comparator, Collection<E> collection) {
		this.comparator = comparator;
		this.elements = new ArrayList<>();
		this.elements.addAll(collection);
		this.elements.sort(comparator);
	}

	// ---------------- Set ----------------

	@Override
	public boolean add(E e) {
		int index = Collections.binarySearch(elements, e, comparator);
		if (index >= 0) {
			return false;
		}
		int insertionPoint = -index - 1;
		elements.add(insertionPoint, e);
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		return Collections.binarySearch(elements, (E) o, comparator) >= 0;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean remove(Object o) {
		int index = Collections.binarySearch(elements, (E) o, comparator);
		if (index < 0) {
			return false;
		}
		elements.remove(index);
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return elements.iterator();
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public void clear() {
		elements.clear();
	}

	// ---------------- SortedSet ----------------

	@Override
	public Comparator<? super E> comparator() {
		return comparator == Comparator.naturalOrder() ? null : comparator;
	}

	@Override
	public E first() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return elements.getFirst();
	}

	@Override
	public E last() {
		if (isEmpty()) {
			throw new NoSuchElementException();
		}
		return elements.getLast();
	}

	@Override
	public Spliterator<E> spliterator() {
		throw new UnsupportedOperationException();
	}

	// ---------------- NavigableSet ----------------


	@Override
	public E lower(E e) {
		int index = Collections.binarySearch(elements, e, comparator);
		int pos = index >= 0 ? index : -index - 1;
		if (pos == 0) {
			return null;
		}
		return elements.get(pos - 1);
	}

	@Override
	public E floor(E e) {
		int index = Collections.binarySearch(elements, e, comparator);
		if (index >= 0) {
			return elements.get(index);
		}
		int pos = -index - 1;
		if (pos == 0) {
			return null;
		}
		return elements.get(pos - 1);
	}

	@Override
	public E ceiling(E e) {
		int index = Collections.binarySearch(elements, e, comparator);
		if (index >= 0) {
			return elements.get(index);
		}
		int pos = -index - 1;
		if (pos == elements.size()) {
			return null;
		}
		return elements.get(pos);
	}

	@Override
	public E higher(E e) {
		int index = Collections.binarySearch(elements, e, comparator);
		int pos = index >= 0 ? index + 1 : -index - 1;
		if (pos >= elements.size()) {
			return null;
		}
		return elements.get(pos);
	}

	@Override
	public E pollFirst() {
		if (isEmpty()) {
			return null;
		}
		return elements.removeFirst();
	}

	@Override
	public E pollLast() {
		if (isEmpty()) {
			return null;
		}
		return elements.removeLast();
	}

	@Override
	public NavigableSet<E> descendingSet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> descendingIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		throw new UnsupportedOperationException();
	}

	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return subSet(first(), true, toElement, inclusive);
	}

	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return subSet(fromElement, inclusive, last(), true);
	}

	@Override
	public SortedSet<E> subSet(E fromElement, E toElement) {
		return subSet(fromElement, true, toElement, false);
	}

	@Override
	public SortedSet<E> headSet(E toElement) {
		return headSet(toElement, false);
	}

	@Override
	public SortedSet<E> tailSet(E fromElement) {
		return tailSet(fromElement, true);
	}
}


