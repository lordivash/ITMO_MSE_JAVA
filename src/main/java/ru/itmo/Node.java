package ru.itmo;

import org.apache.commons.lang3.NotImplementedException;

public class Node {
    Node(Alphabet alphabet) {
        this.alphabet = alphabet;
        children = new Node[alphabet.length()];
    }

    public void setTerminalOff() {
        isTerminal = false;
    }

    public void setTerminalOn() {
        isTerminal = true;
    }

    public int size() {
        return size;
    }

    int howManyStartsWithPrefix(String element, int i) {
        if (i == element.length()) {
            return size;
        }

        int pos = alphabet.getPos(element.charAt(i));
        return ((children[pos] != null) ? children[pos].howManyStartsWithPrefix(element, i + 1) : 0);
    }

    // Works only if trie contains element
    public boolean remove(String element, int i) {
        if (i == element.length()) {
            setTerminalOff();
            --size;
            return true;
        }

        int pos = alphabet.getPos(element.charAt(i));
        boolean result = children[pos].remove(element, i + 1);
        --size;

        return result;
    }

    // Works only if the element doesn't exist
    public boolean add(String element, int i) {
        if (i == element.length()) {
            setTerminalOn();
            ++size;
            return true;
        }

        int pos = alphabet.getPos(element.charAt(i));

        if (children[pos] == null) {
            children[pos] = new Node(alphabet);
        }

        boolean result = children[pos].add(element, i + 1);
        ++size;

        return result;
    }

    public boolean contains(String element, int i) {
        if (i == element.length()) {
            return isTerminal;
        }

        int pos = alphabet.getPos(element.charAt(i));
        if (children[pos] != null) {
            return children[pos].contains(element, i + 1);
        } else {
            return false;
        }
    }

    private final Alphabet alphabet;
    private final Node[] children;
    private boolean isTerminal = false;
    private int size = 0;
}
