package ru.itmo;

import java.util.HashMap;

public class Alphabet {
    Alphabet(String alphabet) {
        this.alphabet = alphabet;
        pos = new HashMap<Character, Integer>();

        for (int i = 0; i < alphabet.length(); ++i) {
            pos.put(alphabet.charAt(i), i);
        }
    }

    public int getPos(char c) {
        return pos.get(c);
    }

    public int length() {
        return alphabet.length();
    }

    public String getAlphabet() {
        return alphabet;
    }

    private final String alphabet;
    private final HashMap<Character, Integer> pos;
}
