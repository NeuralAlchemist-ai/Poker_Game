package com.poker;

public class Card implements Comparable<Card> {
    private final Rank rank;
    private final Suit suit;
    
    public Card(Rank rank, Suit suit) {
        if (rank == null || suit == null) {
            throw new IllegalArgumentException("Rank and suit cannot be null");
        }
        this.rank = rank;
        this.suit = suit;
    }
    
    public Rank getRank() {
        return rank;
    }
    
    public Suit getSuit() {
        return suit;
    }
    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.rank.getValue(), other.rank.getValue());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        Card other = (Card) obj;
        return this.rank == other.rank && this.suit == other.suit;
    }
    
    @Override
    public int hashCode() {
        return 31 * rank.hashCode() + suit.hashCode();
    }
    
    @Override
    public String toString() {
        return rank.getSymbol() + suit.getSymbol();
    }
    
    public String toDisplayString() {
        return rank.getSymbol() + suit.getUnicodeSymbol();
    }
    
    public static Card fromString(String cardStr) {
        if (cardStr == null || cardStr.length() < 2) {
            throw new IllegalArgumentException("Invalid card string: " + cardStr);
        }
        
        String rankStr = cardStr.substring(0, cardStr.length() - 1);
        String suitStr = cardStr.substring(cardStr.length() - 1);
        
        Rank rank = Rank.fromSymbol(rankStr);
        Suit suit = Suit.fromSymbol(suitStr);
        
        return new Card(rank, suit);
    }
}


