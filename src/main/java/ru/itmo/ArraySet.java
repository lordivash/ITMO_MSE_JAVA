package ru.itmo;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.Spliterator;

public class ArraySet<E extends Comparable<? super E>> extends AbstractSet<E> implements NavigableSet<E> {

	public ArraySet() {}

	public ArraySet(Comparator<? super E> comparator) {}

	public ArraySet(Collection<E> collection) {}

	public ArraySet(Collection<E> collection, Comparator<? super E> comparator) {}

	// ---------------- Set ----------------

	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	// ---------------- SortedSet ----------------

	@Override
	public Comparator<? super E> comparator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E first() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E last() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Spliterator<E> spliterator() {
		throw new UnsupportedOperationException();
	}

	// ---------------- NavigableSet ----------------


	@Override
	public E lower(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E floor(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E ceiling(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E higher(E e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E pollFirst() {
		throw new UnsupportedOperationException();
	}

	@Override
	public E pollLast() {
		throw new UnsupportedOperationException();
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


