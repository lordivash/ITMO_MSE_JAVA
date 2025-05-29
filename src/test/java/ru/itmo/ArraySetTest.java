package ru.itmo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArraySetTest {
	@Test
	public void testSimple() {
		Set<Integer> set = createSet();
		assertTrue(set.add(1));
		assertFalse(set.add(1));
		assertTrue(set.add(2));
		assertFalse(set.remove(3));
		assertTrue(set.remove(2));
	}

	@Test
	public void testNavigable() {
		NavigableSet<String> set = createSet("1", "3", "5");
		assertNull(set.floor("0"));
		assertEquals("1", set.floor("1"));
		assertEquals("1", set.floor("2"));
		assertEquals("3", set.floor("3"));
		assertEquals("3", set.floor("4"));
		assertEquals("5", set.floor("5"));
		assertEquals("5", set.floor("6"));
		assertNull(set.lower("0"));
		assertNull(set.lower("1"));
		assertEquals("1", set.lower("2"));
		assertEquals("1", set.lower("3"));
		assertEquals("3", set.lower("4"));
		assertEquals("3", set.lower("5"));
		assertEquals("5", set.lower("6"));
		assertEquals("1", set.ceiling("0"));
		assertEquals("1", set.ceiling("1"));
		assertEquals("3", set.ceiling("2"));
		assertEquals("3", set.ceiling("3"));
		assertEquals("5", set.ceiling("5"));
		assertNull(set.ceiling("6"));
		assertEquals("1", set.higher("0"));
		assertEquals("3", set.higher("1"));
		assertEquals("3", set.higher("2"));
		assertEquals("5", set.higher("3"));
		assertEquals("5", set.higher("4"));
		assertNull(set.higher("5"));
		assertNull(set.higher("6"));
		assertEquals("5", set.ceiling("4"));
		assertNull(set.ceiling("6"));
		assertNull(set.higher("5"));
		assertEquals("1", set.pollFirst());
		assertEquals("5", set.pollLast());
	}

	@Test
	public void testCmp() {
		NavigableSet<IntHolder> set = createSet(INT_HOLDER_DESCENDING_COMPARATOR, hold(1), hold(3), hold(5));
		IntHolder result = set.higher(hold(5));
		assertNotNull(result);
		assertEquals(3, result.getValue());
	}

	@Test
	public void testNull() {
		Assertions.assertThrows(NullPointerException.class, () -> {
			Set<Integer> set = createSet(1);
			set.add(null);
		});
	}

	@Test
	public void testIterator() {
		NavigableSet<Integer> set = createSet(1, 2, 3, 4);
		NavigableSet<Integer> treeSet = new TreeSet<>(Arrays.asList(1, 2, 3, 4));

		Iterator<Integer> setIterator = set.iterator();
		Iterator<Integer> treeSetIterator = treeSet.iterator();

		while (!treeSet.isEmpty()) {
			assertEquals(setIterator.next(), treeSetIterator.next());
			setIterator.remove();
			treeSetIterator.remove();
			assertEquals(set.size(), treeSet.size());
			assertEquals(setIterator.hasNext(), treeSetIterator.hasNext());
			assertEqualsState(set, treeSet);
		}
	}

	@Test
	public void testDescendingIterator() {
		NavigableSet<Integer> set = createSet(1, 2, 3, 4);
		NavigableSet<Integer> treeSet = new TreeSet<>(Arrays.asList(1, 2, 3, 4));

		Iterator<Integer> setIterator = set.descendingIterator();
		Iterator<Integer> treeSetIterator = treeSet.descendingIterator();

		while (!treeSet.isEmpty()) {
			assertEquals(setIterator.next(), treeSetIterator.next());
			setIterator.remove();
			treeSetIterator.remove();
			assertEquals(set.size(), treeSet.size());
			assertEquals(setIterator.hasNext(), treeSetIterator.hasNext());
			assertEqualsState(set, treeSet);
		}
	}

	@Test
	public void testSubset() {
		NavigableSet<Integer> set = createSet(1, 2, 3, 4);
		NavigableSet<Integer> subSet = set.subSet(0, true, 3, false);
		assertEquals(2, subSet.size());
		assertNull(subSet.higher(2));
		assertNull(subSet.lower(1));
		assertEquals((Integer) 2, subSet.floor(3));

		assertEqualsState(subSet, Arrays.asList(1, 2));
		set.add(0);
		assertEqualsState(subSet, Arrays.asList(0, 1, 2));

		subSet.remove(1);
		assertEqualsState(set, Arrays.asList(0, 2, 3, 4));
	}

	@Test
	public void testRandom() {
		Random rand = new Random(31);
		NavigableSet<IntHolder> set = createSet(INT_HOLDER_DESCENDING_COMPARATOR);
		TreeSet<IntHolder> treeSet = new TreeSet<>(INT_HOLDER_DESCENDING_COMPARATOR);

		final int ITER = 5000;
		final int X = 1000;

		for (int i = 0; i < ITER; i++) {
			int op = rand.nextInt(6);
			int x = rand.nextInt(X);
			IntHolder xHolded = hold(x);
			if (op == 0) {
				assertEquals(set.add(xHolded), treeSet.add(xHolded));
			} else if (op == 1) {
				assertEquals(set.remove(xHolded), treeSet.remove(xHolded));
			} else if (op == 2) {
				assertEquals(set.contains(xHolded), treeSet.contains(xHolded));
			} else if (op == 3) {
				assertEquals(set.size(), treeSet.size());
			} else if (op == 4) {
				assertEquals(set.lower(xHolded), treeSet.lower(xHolded));
			} else if (op == 5) {
				assertEquals(set.ceiling(xHolded), treeSet.ceiling(xHolded));
			}
			assertEqualsState(set, treeSet);
		}
	}

	private static <E> void assertEqualsState(Iterable<E> a, Iterable<E> b) {
		Iterator<E> aIterator = a.iterator();
		Iterator<E> bIterator = b.iterator();
		assertEquals(aIterator.hasNext(), bIterator.hasNext());
		while (aIterator.hasNext()) {
			assertEquals(aIterator.next(), bIterator.next());
			assertEquals(aIterator.hasNext(), bIterator.hasNext());
		}
	}

	private static class IntHolder implements Comparable<IntHolder> {
		int value;

		IntHolder(int value) {
			this.value = value;
		}

		int getValue() {
			return value;
		}

		@Override
		public int compareTo(IntHolder o) {
			return value - o.value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			IntHolder intHolder = (IntHolder) o;
			return value == intHolder.value;
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
	}

	private static IntHolder hold(int value) {
		return new IntHolder(value);
	}

	private final Comparator<IntHolder> INT_HOLDER_DESCENDING_COMPARATOR = Comparator.reverseOrder();

	private static <E extends Comparable<? super E>> NavigableSet<E> createSet() {
		return new ArraySet<>();
	}

	private static <E extends Comparable<? super E>> NavigableSet<E> createSet(Comparator<? super E> cmp) {
		return new ArraySet<E>(cmp);
	}

	@SafeVarargs
	private static <E extends Comparable<? super E>> NavigableSet<E> createSet(E... data) {
		return new ArraySet<>(Arrays.asList(data));
	}

	@SafeVarargs
	private static <E extends Comparable<? super E>> NavigableSet<E> createSet(Comparator<? super E> cmp, E... data) {
		return new ArraySet<>(Arrays.asList(data), cmp);
	}
}
