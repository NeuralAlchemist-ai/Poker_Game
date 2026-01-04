package com.poker;

import java.util.Random;

public class ComputerPlayer extends Player {
    private double daringLevel; 
    private Random random;
    
    public ComputerPlayer(String name, int chips, double daringLevel) {
        super(name, chips);
        this.daringLevel = Math.max(0.0, Math.min(1.0, daringLevel));
        this.random = new Random();
    }

    @Override
    public String makeDecision(int potAmount, int currentBet, int minRaise) {
        int callAmount = currentBet - this.currentBet;
        
        double handStrength = evaluateHandStrength();
        double aggressionFactor = daringLevel + handStrength;
        
        System.out.println(name + " is thinking...");
        
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        if (handStrength < 0.2 && callAmount > chips * 0.1) {
            return "FOLD";
        }
        if (callAmount > chips) {
            return "ALL_IN";
        } else if (aggressionFactor > 1.2 && chips > callAmount + minRaise) {
            int raiseAmount = (int)(minRaise * (1 + daringLevel));
            int totalBet = currentBet + Math.min(raiseAmount, chips - callAmount);
            return "RAISE:" + totalBet;
        } else if (callAmount == 0) {
            return "CHECK";
        } else if (callAmount <= chips && handStrength > 0.3) {
            return "CALL:" + callAmount;
        } else {
            if (chips <= potAmount * 0.2 && random.nextDouble() < daringLevel) return "ALL_IN";
            return "FOLD";
        }
    }
    
    
    private double evaluateHandStrength() {
        if (hand.isEmpty()) return 0.0;
        
        double strength = 0.0;
        
       
        for (Card card : hand) {
            if (card.getRank().getValue() >= Rank.JACK.getValue()) {
                strength += 0.3;
            } else if (card.getRank().getValue() >= Rank.NINE.getValue()) {
                strength += 0.15;
            }
        }

        
        if (hand.size() == 2 && hand.get(0).getRank() == hand.get(1).getRank()) {
            strength += 0.4;
        }
        
        strength += random.nextDouble() * 0.2;
        
        return Math.min(1.0, strength);
    }
    
    
    @Override
    public boolean isComputer() {
        return true;
    }
    
    public double getDaringLevel() {
        return daringLevel;
    }
    
    public void setDaringLevel(double daringLevel) {
        this.daringLevel = Math.max(0.0, Math.min(1.0, daringLevel));
    }
}
