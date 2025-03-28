package ru.itmo;
import ru.itmo.Node;
import ru.itmo.Alphabet;

public class Trie {
	Trie(Alphabet alphabet) {
		this.root = new Node(alphabet);
	}

	/**
	 * Expected complexity: O(|element|)
	 * @return true if this set did not already contain the specified element
	 */
	public boolean add(String element) {
		if (!contains(element)) {
			return root.add(element, 0);
		} else {
			return false;
		}
	}

	/**
	 * Expected complexity: O(|element|)
	 */
	public boolean contains(String element) {
		return root.contains(element, 0);
	}

	/**
	 * Expected complexity: O(|element|)
	 * @return true if this set contained the specified element
	 */
	public boolean remove(String element) {
		if (contains(element)) {
			return root.remove(element, 0);
		} else {
			return false;
		}
	}

	/**
	 * Expected complexity: O(1)
	 */
	public int size() {
		return root.size();
	}

	/**
	 * Expected complexity: O(|prefix|)
	 */
	public int howManyStartsWithPrefix(String prefix) {
		return root.howManyStartsWithPrefix(prefix, 0);
	}

	/**
	 * Get String in trie, next after [element] in k elements
	 * @return found String or null
	 */
	public String nextString(String element, int k) {
//		return root.nextString(element, k , 0);
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private final Node root;
}
