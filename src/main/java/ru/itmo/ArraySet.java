package ru.itmo;

import org.apache.commons.lang3.NotImplementedException;

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

	private static class ArraySubSet<E extends Comparable<? super E>> extends AbstractSet<E> implements NavigableSet<E> {
		private final ArraySet<E> parent;
		private final E fromElement;
		private final E toElement;
		private final boolean fromInclusive;
		private final boolean toInclusive;
		private final Comparator<? super E> comparator;

		ArraySubSet(ArraySet<E> parent, E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
			this.parent = parent;
			this.fromElement = fromElement;
			this.toElement = toElement;
			this.fromInclusive = fromInclusive;
			this.toInclusive = toInclusive;
			this.comparator = parent.comparator;
		}

		private boolean inRange(E e) {
			Comparator<? super E> cmp = comparator;
			if (fromElement != null) {
				int cmpFrom = cmp.compare(e, fromElement);
				if (cmpFrom < 0 || (cmpFrom == 0 && !fromInclusive)) {
					return false;
				}
			}
			if (toElement != null) {
				int cmpTo = cmp.compare(e, toElement);
                return cmpTo <= 0 && (cmpTo != 0 || toInclusive);
			}
			return true;
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean contains(Object o) {
			if (!inRange((E) o)) {
				return false;
			}
			return parent.contains(o);
		}

		@Override
		public boolean add(E e) {
			if (!inRange(e)) {
				throw new IllegalArgumentException();
			}
			return parent.add(e);
		}

		@Override
		@SuppressWarnings("unchecked")
		public boolean remove(Object o) {
			if (!inRange((E) o)) {
				return false;
			}
			return parent.remove(o);
		}

		@Override
		public int size() {
			if (parent.isEmpty()) {
				return 0;
			}
			int fromIndex = findFromIndex();
			int toIndex = findToIndex();
			if (fromIndex > toIndex) {
				return 0;
			}
			return toIndex - fromIndex + 1;
		}

		private int findFromIndex() {
			if (fromElement == null) {
				return 0;
			}
			int idx = Collections.binarySearch(parent.elements, fromElement, comparator);
			if (idx >= 0) {
				return fromInclusive ? idx : idx + 1;
			} else {
				return -idx - 1;
			}
		}

		private int findToIndex() {
			if (toElement == null) {
				return parent.size() - 1;
			}
			int idx = Collections.binarySearch(parent.elements, toElement, comparator);
			if (idx >= 0) {
				return toInclusive ? idx : idx - 1;
			} else {
				return -idx - 2;
			}
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public E first() {
			if (isEmpty()) {
				throw new NoSuchElementException();
			}
			return parent.elements.get(findFromIndex());
		}

		@Override
		public E last() {
			if (isEmpty()) {
				throw new NoSuchElementException();
			}
			return parent.elements.get(findToIndex());
		}

		@Override
		public Comparator<? super E> comparator() {
			return comparator;
		}

		@Override
		public E lower(E e) {
			int idx = Collections.binarySearch(parent.elements, e, comparator);
			int insertionPoint = idx >= 0 ? idx : -idx - 1;
			if (insertionPoint == 0) {
				return null;
			}
			E candidate = parent.elements.get(insertionPoint - 1);
			if (inRange(candidate)) {
				return candidate;
			}
			return null;
		}

		@Override
		public E floor(E e) {
			int index = Collections.binarySearch(parent.elements, e, comparator);
			if (index >= 0) {
				E candidate = parent.elements.get(index);
				if (inRange(candidate)) {
					return candidate;
				}
				return lower(e);
			}
			int insertionPoint = -index - 1;
			if (insertionPoint == 0) {
				return null;
			}
			E candidate = parent.elements.get(insertionPoint - 1);
			if (inRange(candidate)) {
				return candidate;
			}
			return null;
		}

		@Override
		public E ceiling(E e) {
			int index = Collections.binarySearch(parent.elements, e, comparator);
			if (index >= 0) {
				E candidate = parent.elements.get(index);
				if (inRange(candidate)) {
					return candidate;
				}
				return higher(e);
			}
			int insertionPoint = -index - 1;
			if (insertionPoint == parent.size()) {
				return null;
			}
			E candidate = parent.elements.get(insertionPoint);
			if (inRange(candidate)) {
				return candidate;
			}
			return null;
		}

		@Override
		public E higher(E e) {
			int index = Collections.binarySearch(parent.elements, e, comparator);
			int insertionPoint = index >= 0 ? index + 1 : -index - 1;
			if (insertionPoint >= parent.size()) {
				return null;
			}
			E candidate = parent.elements.get(insertionPoint);
			if (inRange(candidate)) {
				return candidate;
			}
			return null;
		}

		@Override
		public E pollFirst() {
			if (isEmpty()) {
				return null;
			}
			E first = first();
			parent.remove(first);
			return first;
		}

		@Override
		public E pollLast() {
			if (isEmpty()) {
				return null;
			}
			E last = last();
			parent.remove(last);
			return last;
		}

		@Override
		public NavigableSet<E> descendingSet() {
//			return new DescendingArraySet<>(this);
			throw new UnsupportedOperationException();
		}

		@Override
		public Iterator<E> descendingIterator() {
			return new Iterator<>() {
				private int index = findToIndex();
				private final int start = findFromIndex();

				@Override
				public boolean hasNext() {
					return index >= start;
				}

				@Override
				public E next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					return parent.elements.get(index--);
				}
			};
		}

		@Override
		public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
			return new ArraySubSet<>(parent, fromElement, fromInclusive, toElement, toInclusive);
		}

		@Override
		public SortedSet<E> subSet(E fromElement, E toElement) {
			return new ArraySubSet<>(parent, fromElement, fromInclusive, toElement, false);
		}

		@Override
		public SortedSet<E> tailSet(E fromElement) {
			return new ArraySubSet<>(parent, fromElement, fromInclusive, toElement, true);
		}

		@Override
		public SortedSet<E> headSet(E toElement) {
			return new ArraySubSet<>(parent, fromElement, fromInclusive, toElement, false);
		}

		@Override
		public NavigableSet<E> headSet(E toElement, boolean inclusive) {
			return new ArraySubSet<>(parent, fromElement, fromInclusive, toElement, inclusive);
		}

		@Override
		public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
			return new ArraySubSet<>(parent, fromElement, inclusive, toElement, toInclusive);
		}

		@Override
		public Iterator<E> iterator() {
			return new Iterator<>() {
				private int index = findFromIndex();
				private final int end = findToIndex();
				private int last = -1;

				@Override
				public boolean hasNext() {
					return index <= end;
				}

				@Override
				public E next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}
					last = index;
					return parent.elements.get(index++);
				}

				@Override
				public void remove() {
					if (last == -1) {
						throw new IllegalStateException();
					}
					parent.elements.remove(last);
					index--;
					last = -1;
				}
			};
		}
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
//		throw new UnsupportedOperationException();
		return new ArraySubSet<>(this, fromElement, fromInclusive, toElement, toInclusive);
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


