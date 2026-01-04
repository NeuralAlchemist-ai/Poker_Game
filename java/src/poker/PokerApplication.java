package com.poker;

import java.util.*;
import java.io.*;

public class PokerApplication {
    private List<Player> players = new ArrayList<>();
    private List<Card> communityCards = new ArrayList<>();
    private List<Card> deck = new ArrayList<>();
    private int pot = 0;
    private int currentBet = 0;
    private int smallBlind = 10, bigBlind = 20;
    private int dealerPosition = 0;
    private Scanner scanner = new Scanner(System.in);
    private CppEvaluator cppEval;
    private FileManager fileManager = new FileManager();

    public void start() {
        try { cppEval = new CppEvaluator(); } catch (Exception e) { cppEval = null; }
        while (true) {
            clearScreen();
            System.out.println("=== POKER GAME ===");
            System.out.println("1. New Game");
            System.out.println("2. Load Game");
            System.out.println("3. Exit");
            System.out.print("Choice: ");
            int c = getIntInput(1,3);
            if (c==1) { setupNewGame(); playGame(); }
            else if (c==2) { loadGame(); playGame(); }
            else { exit(); }
        }
    }

            private static class Pot { int amount; List<Player> eligible; Pot(int a,List<Player> e){amount=a;eligible=e;} }

            private void setupNewGame() {
                clearScreen();
                System.out.println("=== NEW GAME SETUP ===\n");
                System.out.print("Player 1 name: ");
                String name1 = scanner.nextLine().trim(); if (name1.isEmpty()) name1 = "Player 1";
                System.out.print("Starting chips (default 1000): ");
                String chipsInput = scanner.nextLine().trim(); int startingChips = chipsInput.isEmpty() ? 1000 : Integer.parseInt(chipsInput);

                int totalPlayers = 2;
                while (true) {
                    System.out.print("Total players (2-6, default 2): ");
                    String tp = scanner.nextLine().trim();
                    if (tp.isEmpty()) { totalPlayers = 2; break; }
                    try { totalPlayers = Integer.parseInt(tp); if (totalPlayers >=2 && totalPlayers <=6) break; } catch (Exception e) {}
                    System.out.println("Please enter a number between 2 and 6.");
                }

                players.clear();
                players.add(new HumanPlayer(name1, startingChips, 1));
                for (int i=2;i<=totalPlayers;i++) {
                    System.out.print("Add Human or Computer for seat " + i + "? (H/C, default C): ");
                    String t = scanner.nextLine().trim().toUpperCase(); if (t.isEmpty()) t = "C";
                    if (t.equals("H")) {
                        System.out.print("Player " + i + " name: "); String hn = scanner.nextLine().trim(); if (hn.isEmpty()) hn = "Player " + i;
                        players.add(new HumanPlayer(hn, startingChips, i));
                    } else {
                        String cpuName = "CPU " + (i-1);
                        System.out.print("Computer name (default " + cpuName + "): "); String cn = scanner.nextLine().trim(); if (!cn.isEmpty()) cpuName = cn;
                        int level = 2; while (true) { System.out.print("Difficulty (1-Easy,2-Medium,3-Hard, default 2): "); String lv = scanner.nextLine().trim(); if (lv.isEmpty()) { level=2; break; } try { level = Integer.parseInt(lv); if (level>=1 && level<=3) break; } catch (Exception e) {} }
                        double daring = (level==1?0.25:(level==2?0.5:0.85));
                        players.add(new ComputerPlayer(cpuName, startingChips, daring));
                    }
                }

                System.out.println("\nGame created!");
                System.out.print("Players: "); for (Player p: players) System.out.print(p.getName() + " "); System.out.println();
                System.out.println("Starting chips: $" + startingChips);
                pressEnterToContinue();
            }

            private void playGame() {
                boolean playing = true;
                while (playing) {
                    int activeCount=0; Player last=null; for (Player p: players) if (p.getChips()>0) { activeCount++; last=p; }
                    if (activeCount<=1) { if (last!=null) declareWinner(last); break; }
                    playHand();
                    System.out.println("\n=== Hand Complete ===");
                    for (Player p: players) System.out.println(p.getName() + ": $" + p.getChips());
                    System.out.println("\nOptions:"); System.out.println("1. Play next hand"); System.out.println("2. Save game"); System.out.println("3. Return to main menu"); System.out.print("\nChoice: ");
                    int choice = getIntInput(1,3);
                    switch (choice) { case 1: break; case 2: saveGame(); break; case 3: playing=false; break; }
                }
            }

            private void playHand() {
                for (Player p: players) p.resetForNewHand(); communityCards.clear(); pot=0; currentBet=0;
                clearScreen(); System.out.println("\n╔════════════════════════════════════════╗"); System.out.println("║ STARTING NEW HAND ║"); System.out.println("╚════════════════════════════════════════╝\n");
                int num = players.size(); int dealer = dealerPosition % num; int sb = (dealer+1)%num; int bb=(dealer+2)%num;
                System.out.println("Dealer: " + players.get(dealer).getName()); System.out.println("Small Blind ($"+smallBlind+"): " + players.get(sb).getName()); System.out.println("Big Blind ($"+bigBlind+"): " + players.get(bb).getName());
                try { players.get(sb).bet(smallBlind); } catch (Exception e) { players.get(sb).bet(players.get(sb).getChips()); }
                try { players.get(bb).bet(bigBlind); } catch (Exception e) { players.get(bb).bet(players.get(bb).getChips()); }
                pot = smallBlind + bigBlind; currentBet = bigBlind;
                initializeDeck(); dealHoleCards(); pressEnterToContinue();

                if (!bettingRound((dealer+3)%num, "PRE-FLOP")) { endHandWithSidePots(); return; }
                dealFlop(); if (!bettingRound((dealer+1)%num, "FLOP")) { endHandWithSidePots(); return; }
                dealTurn(); if (!bettingRound((dealer+1)%num, "TURN")) { endHandWithSidePots(); return; }
                dealRiver(); if (!bettingRound((dealer+1)%num, "RIVER")) { endHandWithSidePots(); return; }
                endHandWithSidePots();
                dealerPosition = (dealerPosition+1)%num;
            }

            private boolean bettingRound(int firstToActIndex, String roundName) {
                clearScreen(); System.out.println("\n=== " + roundName + " BETTING ===\n"); showCommunityCards();
                int num = players.size();

                List<Player> actionables = new ArrayList<>();
                for (Player p: players) if (!p.hasFolded() && p.getChips() > 0) actionables.add(p);
                if (actionables.isEmpty()) return true;

                int idx = firstToActIndex % num;
                int playersToActSinceRaise = actionables.size();
                int i = 0;

                while (playersToActSinceRaise > 0) {
                    Player p = players.get((idx + i) % num);
                    i = (i + 1) % num;
                    if (p.hasFolded() || p.getChips() <= 0) continue;

                    switchToPlayer(p);
                    String decision = p.makeDecision(pot, currentBet, bigBlind);
                    boolean raised = processDecision(p, decision);

                    if (raised) {
                        actionables.clear(); for (Player q: players) if (!q.hasFolded() && q.getChips() > 0) actionables.add(q);
                        playersToActSinceRaise = actionables.size();
                    } else {
                        playersToActSinceRaise--;
                    }

                    int active = 0; for (Player q: players) if (!q.hasFolded() && (q.getChips()>0 || q.getCurrentBet()>0)) active++;
                    if (active <= 1) break;
                }

                return true;
            }

            private boolean processDecision(Player player, String decision) {
                String[] parts = decision.split(":" ); String action = parts[0];
                boolean raised = false;
                int amountPut = 0;

                switch (action) {
                    case "FOLD":
                        player.fold(); System.out.println(player.getName() + " folds.");
                        break;

                    case "CHECK":
                        System.out.println(player.getName() + " checks.");
                        break;

                    case "CALL": {
                        int callAmount = parts.length>1?Integer.parseInt(parts[1]):(currentBet - player.getCurrentBet());
                        if (callAmount >= player.getChips()) {
                            amountPut = player.getChips();
                            player.bet(amountPut);
                            pot += amountPut;
                            System.out.println(player.getName() + " goes all-in with $"+amountPut+" (call)");
                            if (player.getCurrentBet() > currentBet) { currentBet = player.getCurrentBet(); raised = true; }
                        } else {
                            amountPut = callAmount;
                            player.bet(amountPut);
                            pot += amountPut;
                            System.out.println(player.getName() + " calls $"+callAmount);
                        }
                        break;
                    }

                    case "RAISE": {
                        int desiredTotal = parts.length>1?Integer.parseInt(parts[1]):currentBet;
                        int need = desiredTotal - player.getCurrentBet();
                        if (need >= player.getChips()) {
                            amountPut = player.getChips();
                            player.bet(amountPut);
                            pot += amountPut;
                            System.out.println(player.getName()+" goes ALL_IN for $"+amountPut+" (intended raise)");
                            if (player.getCurrentBet() > currentBet) { currentBet = player.getCurrentBet(); raised = true; }
                        } else {
                            amountPut = need;
                            player.bet(amountPut);
                            pot += amountPut;
                            if (player.getCurrentBet() > currentBet) { currentBet = player.getCurrentBet(); raised = true; }
                            System.out.println(player.getName()+" raises to $"+player.getCurrentBet());
                        }
                        break;
                    }

                    case "ALL_IN": {
                        int all = player.getChips();
                        if (all>0) {
                            amountPut = all;
                            player.bet(amountPut);
                            pot += amountPut;
                            System.out.println(player.getName()+" goes all-in with $"+amountPut);
                            if (player.getCurrentBet() > currentBet) { currentBet = player.getCurrentBet(); raised = true; }
                        } else {
                            System.out.println(player.getName()+" has no chips to go all-in.");
                        }
                        break;
                    }

                    default:
                        System.out.println("Unknown action from " + player.getName() + ": " + decision);
                }

                pressEnterToContinue();
                return raised;
            }

            private List<Pot> buildSidePots(Map<Player,Integer> contrib) {
                List<Pot> pots = new ArrayList<>();
                Map<Player,Integer> remaining = new LinkedHashMap<>(contrib);
                while (true) {
                    int min = Integer.MAX_VALUE; for (int v: remaining.values()) if (v>0 && v<min) min = v;
                    if (min==Integer.MAX_VALUE) break;
                    List<Player> involved = new ArrayList<>();
                    for (Map.Entry<Player,Integer> e: new ArrayList<>(remaining.entrySet())) {
                        if (e.getValue()>0) { involved.add(e.getKey()); }
                    }
                    int amount = 0;
                    for (Player p: involved) { amount += Math.min(min, remaining.get(p)); remaining.put(p, remaining.get(p)-min); }
                    pots.add(new Pot(amount, new ArrayList<>(involved)));
                }
                return pots;
            }

            private void endHandWithSidePots() {
                clearScreen(); System.out.println("\n=== HAND RESULT ===\n");
                Map<Player,Integer> contrib = new LinkedHashMap<>();
                int totalContrib = 0;
                for (Player p: players) { contrib.put(p, p.getCurrentBet()); totalContrib += p.getCurrentBet(); }

                List<Pot> pots = buildSidePots(contrib);

                List<Player> notFolded = new ArrayList<>(); for (Player p: players) if (!p.hasFolded()) notFolded.add(p);
                if (notFolded.size() == 1) {
                    Player w = notFolded.get(0);
                    System.out.println(w.getName()+" wins $"+totalContrib+" (others folded)"); w.winPot(totalContrib);
                    pressEnterToContinue();
                    return;
                }

                for (Pot sp: pots) {
                    List<Player> elig = new ArrayList<>(); for (Player p: sp.eligible) if (!p.hasFolded()) elig.add(p);
                    if (elig.isEmpty()) continue;
                    Player best = elig.get(0);
                    List<Player> tied = new ArrayList<>(); tied.add(best);
                    for (int i=1;i<elig.size();i++) {
                        Player c = elig.get(i);
                        int res = 0;
                        try { res = cppEval!=null? cppEval.evaluate(best.getHand(), c.getHand(), communityCards) : Integer.compare(evaluateHand(c), evaluateHand(best)); }
                        catch (Exception e) { res = Integer.compare(evaluateHand(c), evaluateHand(best)); }
                        if (res==2) { best = c; tied.clear(); tied.add(c); }
                        else if (res==0) tied.add(c);
                    }
                    int share = sp.amount / tied.size(); for (Player p: tied) p.winPot(share);
                    if (tied.size()==1) System.out.println(tied.get(0).getName()+" wins side-pot $"+sp.amount+"!");
                    else System.out.println("Split side-pot $"+sp.amount+" among " + tied.size() + " players, each gets $"+share);
                }
                pressEnterToContinue();
            }

            private void showCommunityCards() {
                if (communityCards.isEmpty()) { System.out.println("Community cards: (none yet)"); }
                else { System.out.print("Community cards: "); for (Card c: communityCards) System.out.print(c.toDisplayString()+" "); System.out.println("\n"); }
            }

            private void switchToPlayer(Player player) {
                System.out.println("\n" + "=".repeat(50));
                System.out.println(" *** PASS COMPUTER TO " + player.getName().toUpperCase() + " ***");
                System.out.println("=".repeat(50) + "\n");
                pressEnterToContinue(); clearScreen();
            }

            private void saveGame() {
                System.out.print("\nEnter save file name: "); String filename = scanner.nextLine().trim(); if (!filename.endsWith(".txt")) filename += ".txt";
                GameState state = new GameState(); state.setPot(pot); state.setCurrentBet(currentBet); state.setDealerPosition(dealerPosition);
                if (players.size()>=1) { Player p1 = players.get(0); state.setPlayer1Name(p1.getName()); state.setPlayer1Chips(p1.getChips()); state.setPlayer1Cards(p1.getHand()); state.setPlayer1Folded(p1.hasFolded()); }
                if (players.size()>=2) { Player p2 = players.get(1); state.setPlayer2Name(p2.getName()); state.setPlayer2Chips(p2.getChips()); state.setPlayer2Cards(p2.getHand()); state.setPlayer2Folded(p2.hasFolded()); }
                state.setCommunityCards(communityCards);
                try { fileManager.saveGame(filename, state); System.out.println("Saved as " + filename); } catch (Exception e) { System.err.println("Save failed: " + e.getMessage()); }
            }

            private void loadGame() {
                List<String> savedGames = fileManager.listSavedGames(); if (savedGames.isEmpty()) { System.out.println("\nNo saved games found."); return; }
                System.out.println("\n=== SAVED GAMES ==="); for (int i=0;i<savedGames.size();i++) System.out.println((i+1)+". " + savedGames.get(i)); System.out.print("\nSelect game to load (0 to cancel): "); int choice = getIntInput(0, savedGames.size()); if (choice==0) return;
                try {
                    GameState state = fileManager.loadGame(savedGames.get(choice-1));
                    players.clear();
                    players.add(new HumanPlayer(state.getPlayer1Name(), state.getPlayer1Chips(), 1));
                    players.add(new HumanPlayer(state.getPlayer2Name(), state.getPlayer2Chips(), 2));
                    for (Card c: state.getPlayer1Cards()) players.get(0).addCard(c);
                    for (Card c: state.getPlayer2Cards()) players.get(1).addCard(c);
                    pot = state.getPot(); currentBet = state.getCurrentBet(); dealerPosition = state.getDealerPosition(); communityCards = new ArrayList<>(state.getCommunityCards());
                    System.out.println("\nGame loaded successfully!");
                } catch (Exception e) { System.err.println("Load failed: " + e.getMessage()); }
            }

            private void declareWinner(Player winner) {
                clearScreen(); System.out.println("\n╔════════════════════════════════════════╗"); System.out.println("║ GAME OVER! ║"); System.out.println("╚════════════════════════════════════════╝\n"); System.out.println(winner.getName() + " wins the game!"); System.out.println("Final chips: $" + winner.getChips()); pressEnterToContinue();
            }

            public void exit() {
                System.out.println("\nThanks for playing!");
                for (Player p: players) if (p instanceof HumanPlayer) ((HumanPlayer)p).cleanup();
                scanner.close(); if (cppEval!=null) try { cppEval.close(); } catch (Exception e) {}
                System.exit(0);
            }

            private void clearScreen() { System.out.print("\033[H\033[2J"); System.out.flush(); for (int i=0;i<50;i++) System.out.println(); }
            private void pressEnterToContinue() { System.out.print("\nPress ENTER to continue..."); scanner.nextLine(); }
            private int getIntInput(int min,int max) { while (true) { try { int input = scanner.nextInt(); scanner.nextLine(); if (input>=min && input<=max) return input; else System.out.print("Please enter a number between " + min + " and " + max + ": "); } catch (Exception e) { scanner.nextLine(); System.out.print("Invalid input. Try again: "); } } }

            private int evaluateHand(Player player) {
                List<Card> all = new ArrayList<>(player.getHand()); all.addAll(communityCards);
                Collections.sort(all, Collections.reverseOrder()); return all.get(0).getRank().getValue();
            }

            private void initializeDeck() {
                deck = new ArrayList<>(); for (Suit s: Suit.values()) for (Rank r: Rank.values()) deck.add(new Card(r,s)); Collections.shuffle(deck);
            }

            private void dealHoleCards() { for (int i=0;i<2;i++) for (Player p: players) p.addCard(deck.remove(0)); }
            private void dealFlop() { deck.remove(0); communityCards.add(deck.remove(0)); communityCards.add(deck.remove(0)); communityCards.add(deck.remove(0)); }
            private void dealTurn() { deck.remove(0); communityCards.add(deck.remove(0)); }
            private void dealRiver() { deck.remove(0); communityCards.add(deck.remove(0)); }

            public static void main(String[] args) { PokerApplication app = new PokerApplication(); app.start(); }
        }