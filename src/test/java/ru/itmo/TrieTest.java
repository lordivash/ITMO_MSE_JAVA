package ru.itmo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrieTest {

	@Test
	public void test6() {
		Trie trie = new Trie();
		trie.add("AA");
		trie.add("AB");
		assertEquals("AA", trie.nextString("A", 1));
		assertEquals("AB", trie.nextString("AB", 0));
	}
}
