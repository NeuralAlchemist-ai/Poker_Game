package com.poker;

import java.io.*;
import java.util.*;

public class CppEvaluator {
    private Process process;
    private BufferedWriter writer;
    private BufferedReader reader;

    public CppEvaluator() throws IOException {
        String path = System.getenv("POKER_ENGINE_PATH");
        if (path == null || path.isEmpty()) {
            path = "./cpp/bin/poker_engine"; // default relative path
        }

        ProcessBuilder pb = new ProcessBuilder(path);
        pb.redirectErrorStream(true);
        process = pb.start();
        writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public int evaluate(List<Card> p1, List<Card> p2, List<Card> community) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("EVAL ");

        for (int i=0;i<p1.size();++i) { if (i>0) sb.append(','); sb.append(cardToCode(p1.get(i))); }
        sb.append(';');
        for (int i=0;i<p2.size();++i) { if (i>0) sb.append(','); sb.append(cardToCode(p2.get(i))); }
        sb.append(';');
        for (int i=0;i<community.size();++i) { if (i>0) sb.append(','); sb.append(cardToCode(community.get(i))); }

        writer.write(sb.toString());
        writer.newLine();
        writer.flush();

        String resp = reader.readLine();
        if (resp == null) throw new IOException("Engine closed");
        if (resp.startsWith("WINNER:1")) return 1;
        if (resp.startsWith("WINNER:2")) return 2;
        return 0;
    }

    private String cardToCode(Card c) {
        String sym = null;
        try {
            sym = c.getRank().getSymbol();
        } catch (Exception e) {
            sym = c.getRank().toString();
        }
        return sym + suitCode(c.getSuit());
    }

    private String suitCode(Suit s) {
        switch (s) {
            case SPADES: return "S";
            case HEARTS: return "H";
            case CLUBS: return "C";
            case DIAMONDS: return "D";
        }
        return "?";
    }

    public void close() throws IOException {
        try {
            writer.write("EXIT\n");
            writer.flush();
        } catch (Exception e) {}
        try { process.destroy(); } catch (Exception e) {}
    }
}
