package com.poker;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {
    protected String name;
    protected int chips;
    protected List<Card> hand;
    protected boolean hasFolded;
    protected int currentBet;
    
    public Player(String name, int chips) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be empty");
        }
        if (chips < 0) {
            throw new IllegalArgumentException("Chips cannot be negative");
        }
        
        this.name = name;
        this.chips = chips;
        this.hand = new ArrayList<>();
        this.hasFolded = false;
        this.currentBet = 0;
    }
    
    public abstract String makeDecision(int potAmount, int currentBet, int minRaise);
    
    public abstract boolean isComputer();
    
    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }
 
    public void bet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Cannot bet more than available chips");
        }
        chips -= amount;
        currentBet += amount;
    }
    
    public void fold() {
        hasFolded = true;
    }
    
    public void winPot(int amount) {
        chips += amount;
    }
    
    public void resetForNewHand() {
        hand.clear();
        hasFolded = false;
        currentBet = 0;
    }
    
    public String getName() {
        return name;
    }
    
    public int getChips() {
        return chips;
    }
    
    public void setChips(int chips) {
        this.chips = chips;
    }
    
    public List<Card> getHand() {
        return new ArrayList<>(hand);
    }
    
    public boolean hasFolded() {
        return hasFolded;
    }
    
    public int getCurrentBet() {
        return currentBet;
    }
    
    public void resetCurrentBet() {
        currentBet = 0;
    }
    
    @Override
    public String toString() {
        return name + " ($" + chips + ")";
    }
}