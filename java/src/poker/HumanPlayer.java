package com.poker;

import java.util.Scanner;

public class HumanPlayer extends Player {
    private int playerNumber;
    private Scanner scanner;
    
    public HumanPlayer(String name, int chips, int playerNumber) {
        super(name, chips);
        this.playerNumber = playerNumber;
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public String makeDecision(int potAmount, int currentBet, int minRaise) {
        int callAmount = currentBet - this.currentBet;
        
        System.out.println("\n=== " + name + "'s Turn ===");
        System.out.println("Pot: $" + potAmount);
        System.out.println("Current bet: $" + currentBet);
        System.out.println("Your chips: $" + chips);
        System.out.println("To call: $" + callAmount);
        
        System.out.println("\nYour hand:");
        for (Card card : hand) {
            System.out.print(card.toDisplayString() + " ");
        }
        System.out.println("\n");
        
        System.out.println("Options:");
        System.out.println("1. Fold");
        if (callAmount == 0) {
            System.out.println("2. Check");
        } else {
            System.out.println("2. Call ($" + callAmount + ")");
        }
        System.out.println("3. Raise");
        
        System.out.print("\nEnter your choice (1-3): ");
        
        int choice = 0;
        try {
            choice = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid input. Please try again.");
            return makeDecision(potAmount, currentBet, minRaise);
        }
        
        switch (choice) {
            case 1:
                return "FOLD";
            case 2:
                if (callAmount == 0) {
                    return "CHECK";
                } else {
                    return "CALL:" + callAmount;
                }
            case 3:
                System.out.print("Enter raise amount (min $" + minRaise + "): $");
                try {
                    int raiseAmount = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (raiseAmount < minRaise) {
                        System.out.println("Raise too small. Minimum is $" + minRaise);
                        return makeDecision(potAmount, currentBet, minRaise);
                    }
                    
                    int totalBet = currentBet + raiseAmount;
                    int amountNeeded = totalBet - this.currentBet;
                    
                    if (amountNeeded > chips) {
                        System.out.println("Not enough chips!");
                        return makeDecision(potAmount, currentBet, minRaise);
                    }
                    
                    return "RAISE:" + totalBet;
                } catch (Exception e) {
                    scanner.nextLine();
                    System.out.println("Invalid input. Please try again.");
                    return makeDecision(potAmount, currentBet, minRaise);
                }
            default:
                System.out.println("Invalid choice. Please try again.");
                return makeDecision(potAmount, currentBet, minRaise);
        }
    }
    
    @Override
    public boolean isComputer() {
        return false;
    }
    
    public int getPlayerNumber() {
        return playerNumber;
    }
    
    public void cleanup() {
        scanner = null;
    }
}