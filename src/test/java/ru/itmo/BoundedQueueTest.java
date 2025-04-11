package ru.itmo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BoundedQueueTest {

	@Test
	public void testOffer() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(3);
		bq.offer(5);
		bq.offer(1);
		bq.offer(2);
		bq.offer(3);
        assertEquals(5, (int) bq.peek());
	}

	@Test
	public void testPeek() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(5);
		assertNull(bq.peek());
		bq.offer(3);
		assertEquals(3, (int) bq.peek());
	}

	@Test
	public void testElement() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(5);

		Exception caught = null;
		try {
			bq.element();
		} catch (Exception e) {
			caught = e;
		}

        assertInstanceOf(NoSuchElementException.class, caught);

		bq.offer(1);
		assertEquals(1, (int) bq.element());
	}

	@Test
	public void testPoll() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(5);
		assertNull(bq.poll());

		bq.offer(1);
		bq.offer(2);

		assertEquals(1, (int) bq.poll());
		assertEquals(2, (int) bq.poll());

		assertNull(bq.poll());
	}

	@Test
	public void testRemove() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(5);

		Exception caught = null;
		try {
			bq.remove();
		} catch (Exception e) {
			caught = e;
		}

		assertInstanceOf(NoSuchElementException.class, caught);

		bq.offer(1);
		assertEquals(1, (int) bq.remove());
	}

	@Test
	public void testSize() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(3);
		assertEquals(0, bq.size());

		bq.offer(1);
		assertEquals(1, bq.size());

		bq.offer(2);
		assertEquals(2, bq.size());

		bq.offer(3);
		assertEquals(3, bq.size());

		bq.offer(4);
		assertEquals(3, bq.size());

		bq.poll();
		assertEquals(2, bq.size());

		bq.poll();
		assertEquals(1, bq.size());

		bq.poll();
		assertEquals(0, bq.size());

		bq.poll();
		assertEquals(0, bq.size());

		bq.poll();
	}

	@Test
	public void testLastChanged() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(3);
		bq.offer(1);
		bq.offer(2);
		bq.offer(3);
		bq.offer(4);

		bq.poll();
		bq.poll();

		assertEquals(4, (int) bq.poll());
	}

	@Test
	public void testIteratorReadAll() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(3);

		bq.offer(1);
		bq.offer(2);
		bq.offer(3);

		ArrayList<Integer> lst = new ArrayList<>(bq);

		ArrayList<Integer> exp = new ArrayList<>();
		exp.add(1);
		exp.add(2);
		exp.add(3);

        assertEquals(exp, lst);
	}

	@Test
	public void testIteratorRemoveEven() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(7);

		bq.offer(2);
		bq.offer(2);
		bq.offer(3);
		bq.offer(4);
		bq.offer(5);
		bq.offer(7);
		bq.offer(6);

        bq.removeIf(integer -> integer % 2 == 0);
		ArrayList<Integer> lst = new ArrayList<>(bq);

		ArrayList<Integer> exp = new ArrayList<>();
		exp.add(3);
		exp.add(5);
		exp.add(7);

		assertEquals(exp, lst);
	}

	@Test
	public void testIteratorFullRemove() {
		BoundedQueue<Integer> bq = new BoundedQueue<>(7);

		bq.offer(1);
		bq.offer(2);
		bq.offer(3);

		bq.removeIf(integer -> true);

		ArrayList<Integer> lst = new ArrayList<>(bq);
		ArrayList<Integer> exp = new ArrayList<>();

		assertEquals(exp, lst);
	}
}