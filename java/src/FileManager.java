package com.poker;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileManager {
    private static final String SAVE_DIRECTORY = "saves/";
    
    public FileManager() {
        File dir = new File(SAVE_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    public void saveGame(String filename, GameState state) {
        String filepath = SAVE_DIRECTORY + filename;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            writer.write("POT:" + state.getPot());
            writer.newLine();
            writer.write("CURRENT_BET:" + state.getCurrentBet());
            writer.newLine();
            writer.write("DEALER:" + state.getDealerPosition());
            writer.newLine();
            
            writer.write("PLAYER1_NAME:" + state.getPlayer1Name());
            writer.newLine();
            writer.write("PLAYER1_CHIPS:" + state.getPlayer1Chips());
            writer.newLine();
            writer.write("PLAYER1_CARDS:" + cardsToString(state.getPlayer1Cards()));
            writer.newLine();
            writer.write("PLAYER1_FOLDED:" + state.isPlayer1Folded());
            writer.newLine();
            
            writer.write("PLAYER2_NAME:" + state.getPlayer2Name());
            writer.newLine();
            writer.write("PLAYER2_CHIPS:" + state.getPlayer2Chips());
            writer.newLine();
            writer.write("PLAYER2_CARDS:" + cardsToString(state.getPlayer2Cards()));
            writer.newLine();
            writer.write("PLAYER2_FOLDED:" + state.isPlayer2Folded());
            writer.newLine();
            
            writer.write("COMMUNITY_CARDS:" + cardsToString(state.getCommunityCards()));
            writer.newLine();
            
            System.out.println("Game saved successfully to " + filename);
            
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            throw new RuntimeException("Save failed", e);
        }
    }
    
    public GameState loadGame(String filename) {
        String filepath = SAVE_DIRECTORY + filename;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            GameState state = new GameState();
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length != 2) continue;
                
                String key = parts[0];
                String value = parts[1];
                
                switch (key) {
                    case "POT":
                        state.setPot(Integer.parseInt(value));
                        break;
                    case "CURRENT_BET":
                        state.setCurrentBet(Integer.parseInt(value));
                        break;
                    case "DEALER":
                        state.setDealerPosition(Integer.parseInt(value));
                        break;
                    case "PLAYER1_NAME":
                        state.setPlayer1Name(value);
                        break;
                    case "PLAYER1_CHIPS":
                        state.setPlayer1Chips(Integer.parseInt(value));
                        break;
                    case "PLAYER1_CARDS":
                        state.setPlayer1Cards(stringToCards(value));
                        break;
                    case "PLAYER1_FOLDED":
                        state.setPlayer1Folded(Boolean.parseBoolean(value));
                        break;
                    case "PLAYER2_NAME":
                        state.setPlayer2Name(value);
                        break;
                    case "PLAYER2_CHIPS":
                        state.setPlayer2Chips(Integer.parseInt(value));
                        break;
                    case "PLAYER2_CARDS":
                        state.setPlayer2Cards(stringToCards(value));
                        break;
                    case "PLAYER2_FOLDED":
                        state.setPlayer2Folded(Boolean.parseBoolean(value));
                        break;
                    case "COMMUNITY_CARDS":
                        state.setCommunityCards(stringToCards(value));
                        break;
                }
            }
            
            System.out.println("Game loaded successfully from " + filename);
            return state;
            
        } catch (FileNotFoundException e) {
            System.err.println("Save file not found: " + filename);
            throw new RuntimeException("Load failed - file not found", e);
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            throw new RuntimeException("Load failed", e);
        }
    }
    
    public List<String> listSavedGames() {
        List<String> savedGames = new ArrayList<>();
        File dir = new File(SAVE_DIRECTORY);
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                savedGames.add(file.getName());
            }
        }
        
        return savedGames;
    }
    
    private String cardsToString(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cards.size(); i++) {
            sb.append(cards.get(i).toString());
            if (i < cards.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
    
    private List<Card> stringToCards(String cardsStr) {
        List<Card> cards = new ArrayList<>();
        if (cardsStr == null || cardsStr.trim().isEmpty()) {
            return cards;
        }
        
        String[] cardStrings = cardsStr.split(",");
        for (String cardStr : cardStrings) {
            try {
                cards.add(Card.fromString(cardStr.trim()));
            } catch (Exception e) {
                System.err.println("Failed to parse card: " + cardStr);
            }
        }
        
        return cards;
    }
}
