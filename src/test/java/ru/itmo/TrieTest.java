package ru.itmo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class TrieTest {
	private Trie trie;

	@BeforeEach
	public void setUp() {
		// Предполагаем, что Alphabet поддерживает символы 'a'-'z'
		Alphabet alphabet = new Alphabet("abcdefghijklmnopqrstuvwxyz"); // Замените на реальный класс
		trie = new Trie(alphabet);
	}

	// Тесты для метода add()
	@Test
	public void testAddNewElement() {
		assertTrue(trie.add("test"));
		assertTrue(trie.contains("test"));
		assertEquals(1, trie.size());
	}

	@Test
	public void testAddDuplicateElement() {
		trie.add("test");
		assertFalse(trie.add("test")); // Повторное добавление
		assertEquals(1, trie.size());
	}

	// Тесты для метода contains()
	@Test
	public void testContainsNonExistentElement() {
		assertFalse(trie.contains("test"));
	}

	@Test
	public void testContainsAfterRemove() {
		trie.add("test");
		trie.remove("test");
		assertFalse(trie.contains("test"));
	}

	// Тесты для метода remove()
	@Test
	public void testRemoveExistingElement() {
		trie.add("test");
		assertTrue(trie.remove("test"));
		assertEquals(0, trie.size());
	}

	@Test
	public void testRemoveNonExistentElement() {
		assertFalse(trie.remove("test"));
	}

	// Тесты для метода size()
	@Test
	public void testSizeAfterMultipleOperations() {
		assertEquals(0, trie.size());
		trie.add("one");
		assertEquals(1, trie.size());
		trie.add("two");
		assertEquals(2, trie.size());
		trie.remove("one");
		assertEquals(1, trie.size());
	}

	// Тесты для метода howManyStartsWithPrefix()
	@Test
	public void testHowManyStartsWithPrefix() {
		trie.add("test");
		trie.add("testing");
		trie.add("team");
		trie.add("apple");

		assertEquals(3, trie.howManyStartsWithPrefix("te"));
		assertEquals(2, trie.howManyStartsWithPrefix("tes"));
		assertEquals(1, trie.howManyStartsWithPrefix("app"));
		assertEquals(4, trie.howManyStartsWithPrefix("")); // Все элементы
		assertEquals(0, trie.howManyStartsWithPrefix("xyz"));
	}

	@Test
	public void testPrefixAfterRemoval() {
		trie.add("app");
		trie.add("apple");
		trie.remove("apple");

		assertEquals(1, trie.howManyStartsWithPrefix("app"));
		assertTrue(trie.contains("app"));
	}

	// Тест для пустой строки
	@Test
	public void testEmptyString() {
		assertTrue(trie.add(""));
		assertTrue(trie.contains(""));
		assertEquals(1, trie.size());

		assertTrue(trie.remove(""));
		assertFalse(trie.contains(""));
		assertEquals(0, trie.size());
	}

	// Тест на вложенные префиксы
	@Test
	public void testNestedPrefixes() {
		trie.add("hello");
		trie.add("helloworld");
		trie.add("helium");

		assertEquals(3, trie.howManyStartsWithPrefix("he"));
		trie.remove("helloworld");
		assertEquals(2, trie.howManyStartsWithPrefix("he"));
	}
}
