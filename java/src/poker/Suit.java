package com.poker;

public enum Suit {
    HEARTS("H", "♥"),
    DIAMONDS("D", "♦"),
    CLUBS("C", "♣"),
    SPADES("S", "♠");

    private final String symbol;
    private final String unicode;

    Suit(String symbol, String unicode) {
        this.symbol = symbol;
        this.unicode = unicode;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getUnicodeSymbol() {
        return unicode;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public static Suit fromSymbol(String sym) {
        for (Suit s : values()) {
            if (s.symbol.equals(sym)) return s;
        }
        throw new IllegalArgumentException("Invalid suit symbol: " + sym);
    }
}

