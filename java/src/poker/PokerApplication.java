package com.poker;
import java.util.*;

public class PokerApplication {
    private HumanPlayer player1;
    private HumanPlayer player2;
    private List<Card> deck;
    private List<Card> communityCards;
    private int pot;
    private int currentBet;
    private int dealerPosition;
    private int smallBlind = 10;
    private int bigBlind = 20;
    private FileManager fileManager;
    private Scanner scanner;
   
    public PokerApplication() {
        this.communityCards = new ArrayList<>();
        this.pot = 0;
        this.currentBet = 0;
        this.dealerPosition = 0;
        this.fileManager = new FileManager();
        this.scanner = new Scanner(System.in);
    }
   
    public void start() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║ TEXAS HOLD'EM POKER - HOT SEAT ║");
        System.out.println("╚════════════════════════════════════════╝\n");
       
        showMainMenu();
    }
   
    private void showMainMenu() {
        while (true) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. New Game");
            System.out.println("2. Load Game");
            System.out.println("3. Exit");
            System.out.print("\nChoice: ");
           
            int choice = getIntInput(1, 3);
           
            switch (choice) {
                case 1:
                    setupNewGame();
                    playGame();
                    break;
                case 2:
                    loadGame();
                    if (player1 != null && player2 != null) {
                        playGame();
                    }
                    break;
                case 3:
                    exit();
                    return;
            }
        }
    }
   
    private void setupNewGame() {
        clearScreen();
        System.out.println("=== NEW GAME SETUP ===\n");
       
        System.out.print("Player 1 name: ");
        String name1 = scanner.nextLine().trim();
        if (name1.isEmpty()) name1 = "Player 1";
       
        System.out.print("Player 2 name: ");
        String name2 = scanner.nextLine().trim();
        if (name2.isEmpty()) name2 = "Player 2";
       
        System.out.print("Starting chips (default 1000): ");
        String chipsInput = scanner.nextLine().trim();
        int startingChips = chipsInput.isEmpty() ? 1000 : Integer.parseInt(chipsInput);
       
        player1 = new HumanPlayer(name1, startingChips, 1);
        player2 = new HumanPlayer(name2, startingChips, 2);
       
        System.out.println("\nGame created!");
        System.out.println(player1.getName() + " vs " + player2.getName());
        System.out.println("Starting chips: $" + startingChips);
       
        pressEnterToContinue();
    }
   
    private void playGame() {
        boolean playing = true;
       
        while (playing) {
            if (player1.getChips() <= 0) {
                declareWinner(player2);
                break;
            }
            if (player2.getChips() <= 0) {
                declareWinner(player1);
                break;
            }
           
            playHand();
           
            System.out.println("\n=== Hand Complete ===");
            System.out.println(player1.getName() + ": $" + player1.getChips());
            System.out.println(player2.getName() + ": $" + player2.getChips());
           
            System.out.println("\nOptions:");
            System.out.println("1. Play next hand");
            System.out.println("2. Save game");
            System.out.println("3. Return to main menu");
            System.out.print("\nChoice: ");
           
            int choice = getIntInput(1, 3);
           
            switch (choice) {
                case 1:
                    break;
                case 2:
                    saveGame();
                    break;
                case 3:
                    playing = false;
                    break;
            }
        }
    }
   
    private void playHand() {
        player1.resetForNewHand();
        player2.resetForNewHand();
        communityCards.clear();
        pot = 0;
        currentBet = 0;
       
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ STARTING NEW HAND ║");
        System.out.println("╚════════════════════════════════════════╝\n");
       
        HumanPlayer dealer = (dealerPosition == 0) ? player1 : player2;
        HumanPlayer smallBlindPlayer = dealer;
        HumanPlayer bigBlindPlayer = (dealer == player1) ? player2 : player1;
       
        System.out.println("Dealer: " + dealer.getName());
        System.out.println("Small Blind ($" + smallBlind + "): " + smallBlindPlayer.getName());
        System.out.println("Big Blind ($" + bigBlind + "): " + bigBlindPlayer.getName());
       
        smallBlindPlayer.bet(smallBlind);
        bigBlindPlayer.bet(bigBlind);
        pot = smallBlind + bigBlind;
        currentBet = bigBlind;
       
        initializeDeck();
       
        dealHoleCards();
       
        pressEnterToContinue();
       
        if (!bettingRound(smallBlindPlayer, bigBlindPlayer, "PRE-FLOP")) {
            endHand();
            return;
        }
       
        dealFlop();
        if (!bettingRound(bigBlindPlayer, smallBlindPlayer, "FLOP")) {
            endHand();
            return;
        }
       
        dealTurn();
        if (!bettingRound(bigBlindPlayer, smallBlindPlayer, "TURN")) {
            endHand();
            return;
        }
       
        dealRiver();
        if (!bettingRound(bigBlindPlayer, smallBlindPlayer, "RIVER")) {
            endHand();
            return;
        }
       
        showdown();
       
        dealerPosition = (dealerPosition + 1) % 2;
    }
   
    private boolean bettingRound(HumanPlayer firstToAct, HumanPlayer secondToAct, String roundName) {
        clearScreen();
        System.out.println("\n=== " + roundName + " BETTING ===\n");
       
        showCommunityCards();
       
        int roundBet = currentBet;
        boolean bettingComplete = false;
        HumanPlayer currentPlayer = firstToAct;
        int actionCount = 0;
       
        while (!bettingComplete) {
            if (currentPlayer.hasFolded()) {
                return false;
            }
           
            switchToPlayer(currentPlayer);
           
            String decision = currentPlayer.makeDecision(pot, currentBet, bigBlind);
            processDecision(currentPlayer, decision);
           
            actionCount++;
           
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
           
            if (actionCount >= 2 && player1.getCurrentBet() == player2.getCurrentBet()) {
                bettingComplete = true;
            }
           
            if (player1.hasFolded() || player2.hasFolded()) {
                return false;
            }
        }
       
        player1.resetCurrentBet();
        player2.resetCurrentBet();
        currentBet = 0;
       
        return true;
    }
   
    private void processDecision(Player player, String decision) {
        String[] parts = decision.split(":");
        String action = parts[0];
       
        switch (action) {
            case "FOLD":
                player.fold();
                System.out.println(player.getName() + " folds.");
                break;
               
            case "CHECK":
                System.out.println(player.getName() + " checks.");
                break;
               
            case "CALL":
                int callAmount = Integer.parseInt(parts[1]);
                player.bet(callAmount);
                pot += callAmount;
                System.out.println(player.getName() + " calls $" + callAmount);
                break;
               
            case "RAISE":
                int newBet = Integer.parseInt(parts[1]);
                int raiseAmount = newBet - player.getCurrentBet();
                player.bet(raiseAmount);
                pot += raiseAmount;
                currentBet = newBet;
                System.out.println(player.getName() + " raises to $" + newBet);
                break;
        }
       
        pressEnterToContinue();
    }
   
    private void endHand() {
        clearScreen();
        System.out.println("\n=== HAND RESULT ===\n");
       
        if (player1.hasFolded()) {
            System.out.println(player2.getName() + " wins $" + pot + " (opponent folded)");
            player2.winPot(pot);
        } else {
            System.out.println(player1.getName() + " wins $" + pot + " (opponent folded)");
            player1.winPot(pot);
        }
       
        pressEnterToContinue();
    }
   
    private void showdown() {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ SHOWDOWN! ║");
        System.out.println("╚════════════════════════════════════════╝\n");
       
        showCommunityCards();
       
        System.out.println("\n" + player1.getName() + "'s hand:");
        for (Card card : player1.getHand()) {
            System.out.print(card.toDisplayString() + " ");
        }
        System.out.println();
       
        System.out.println("\n" + player2.getName() + "'s hand:");
        for (Card card : player2.getHand()) {
            System.out.print(card.toDisplayString() + " ");
        }
        System.out.println("\n");
       
        int score1 = evaluateHand(player1);
        int score2 = evaluateHand(player2);
       
        if (score1 > score2) {
            System.out.println(player1.getName() + " wins $" + pot + "!");
            player1.winPot(pot);
        } else if (score2 > score1) {
            System.out.println(player2.getName() + " wins $" + pot + "!");
            player2.winPot(pot);
        } else {
            System.out.println("Split pot! Each player gets $" + (pot / 2));
            player1.winPot(pot / 2);
            player2.winPot(pot / 2);
        }
       
        pressEnterToContinue();
    }
   
    private int evaluateHand(Player player) {
        List<Card> allCards = new ArrayList<>(player.getHand());
        allCards.addAll(communityCards);
       
        Collections.sort(allCards, Collections.reverseOrder());
       
        return allCards.get(0).getRank().getValue();
    }
   
    private void initializeDeck() {
        deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);
    }
   
    private void dealHoleCards() {
        player1.addCard(deck.remove(0));
        player2.addCard(deck.remove(0));
        player1.addCard(deck.remove(0));
        player2.addCard(deck.remove(0));
    }
   
    private void dealFlop() {
        deck.remove(0);
        communityCards.add(deck.remove(0));
        communityCards.add(deck.remove(0));
        communityCards.add(deck.remove(0));
    }
   
    private void dealTurn() {
        deck.remove(0);
        communityCards.add(deck.remove(0));
    }
   
    private void dealRiver() {
        deck.remove(0);
        communityCards.add(deck.remove(0));
    }
   
    private void showCommunityCards() {
        if (communityCards.isEmpty()) {
            System.out.println("Community cards: (none yet)");
        } else {
            System.out.print("Community cards: ");
            for (Card card : communityCards) {
                System.out.print(card.toDisplayString() + " ");
            }
            System.out.println("\n");
        }
    }
   
    private void switchToPlayer(HumanPlayer player) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println(" *** PASS COMPUTER TO " + player.getName().toUpperCase() + " ***");
        System.out.println("=".repeat(50) + "\n");
        pressEnterToContinue();
        clearScreen();
    }
   
    private void saveGame() {
        System.out.print("\nEnter save file name: ");
        String filename = scanner.nextLine().trim();
        if (!filename.endsWith(".txt")) {
            filename += ".txt";
        }
       
        GameState state = new GameState();
        state.setPot(pot);
        state.setCurrentBet(currentBet);
        state.setDealerPosition(dealerPosition);
        state.setPlayer1Name(player1.getName());
        state.setPlayer1Chips(player1.getChips());
        state.setPlayer1Cards(player1.getHand());
        state.setPlayer1Folded(player1.hasFolded());
        state.setPlayer2Name(player2.getName());
        state.setPlayer2Chips(player2.getChips());
        state.setPlayer2Cards(player2.getHand());
        state.setPlayer2Folded(player2.hasFolded());
        state.setCommunityCards(communityCards);
       
        try {
            fileManager.saveGame(filename, state);
        } catch (Exception e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }
   
    private void loadGame() {
        List<String> savedGames = fileManager.listSavedGames();
       
        if (savedGames.isEmpty()) {
            System.out.println("\nNo saved games found.");
            return;
        }
       
        System.out.println("\n=== SAVED GAMES ===");
        for (int i = 0; i < savedGames.size(); i++) {
            System.out.println((i + 1) + ". " + savedGames.get(i));
        }
       
        System.out.print("\nSelect game to load (0 to cancel): ");
        int choice = getIntInput(0, savedGames.size());
       
        if (choice == 0) return;
       
        try {
            GameState state = fileManager.loadGame(savedGames.get(choice - 1));
           
            player1 = new HumanPlayer(state.getPlayer1Name(), state.getPlayer1Chips(), 1);
            player2 = new HumanPlayer(state.getPlayer2Name(), state.getPlayer2Chips(), 2);
           
            for (Card card : state.getPlayer1Cards()) {
                player1.addCard(card);
            }
            for (Card card : state.getPlayer2Cards()) {
                player2.addCard(card);
            }
           
            pot = state.getPot();
            currentBet = state.getCurrentBet();
            dealerPosition = state.getDealerPosition();
            communityCards = new ArrayList<>(state.getCommunityCards());
           
            System.out.println("\nGame loaded successfully!");
        } catch (Exception e) {
            System.err.println("Load failed: " + e.getMessage());
        }
    }
   
    private void declareWinner(Player winner) {
        clearScreen();
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║ GAME OVER! ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        System.out.println(winner.getName() + " wins the game!");
        System.out.println("Final chips: $" + winner.getChips());
        pressEnterToContinue();
    }
   
    public void exit() {
        System.out.println("\nThanks for playing!");
        player1.cleanup();
        player2.cleanup();
        scanner.close();
        System.exit(0);
    }
   
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
   
    private void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
   
    private int getIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.print("Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (Exception e) {
                scanner.nextLine();
                System.out.print("Invalid input. Try again: ");
            }
        }
    }
   
    public static void main(String[] args) {
        PokerApplication app = new PokerApplication();
        app.start();
    }
}