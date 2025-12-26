package com.poker;
import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int pot;
    private int currentBet;
    private int dealerPosition;
   
    private String player1Name;
    private int player1Chips;
    private List<Card> player1Cards;
    private boolean player1Folded;
   
    private String player2Name;
    private int player2Chips;
    private List<Card> player2Cards;
    private boolean player2Folded;
   
    private List<Card> communityCards;
   
    public GameState() {
        this.player1Cards = new ArrayList<>();
        this.player2Cards = new ArrayList<>();
        this.communityCards = new ArrayList<>();
    }
   
    public int getPot() {
        return pot;
    }
   
    public void setPot(int pot) {
        this.pot = pot;
    }
   
    public int getCurrentBet() {
        return currentBet;
    }
   
    public void setCurrentBet(int currentBet) {
        this.currentBet = currentBet;
    }
   
    public int getDealerPosition() {
        return dealerPosition;
    }
   
    public void setDealerPosition(int dealerPosition) {
        this.dealerPosition = dealerPosition;
    }
   
    public String getPlayer1Name() {
        return player1Name;
    }
   
    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }
   
    public int getPlayer1Chips() {
        return player1Chips;
    }
   
    public void setPlayer1Chips(int player1Chips) {
        this.player1Chips = player1Chips;
    }
   
    public List<Card> getPlayer1Cards() {
        return new ArrayList<>(player1Cards);
    }
   
    public void setPlayer1Cards(List<Card> player1Cards) {
        this.player1Cards = new ArrayList<>(player1Cards);
    }
   
    public boolean isPlayer1Folded() {
        return player1Folded;
    }
   
    public void setPlayer1Folded(boolean player1Folded) {
        this.player1Folded = player1Folded;
    }
   
    public String getPlayer2Name() {
        return player2Name;
    }
   
    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
   
    public int getPlayer2Chips() {
        return player2Chips;
    }
   
    public void setPlayer2Chips(int player2Chips) {
        this.player2Chips = player2Chips;
    }
   
    public List<Card> getPlayer2Cards() {
        return new ArrayList<>(player2Cards);
    }
   
    public void setPlayer2Cards(List<Card> player2Cards) {
        this.player2Cards = new ArrayList<>(player2Cards);
    }
   
    public boolean isPlayer2Folded() {
        return player2Folded;
    }
   
    public void setPlayer2Folded(boolean player2Folded) {
        this.player2Folded = player2Folded;
    }
   
    public List<Card> getCommunityCards() {
        return new ArrayList<>(communityCards);
    }
   
    public void setCommunityCards(List<Card> communityCards) {
        this.communityCards = new ArrayList<>(communityCards);
    }
}