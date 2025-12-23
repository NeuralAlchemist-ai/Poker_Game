package com.poker.model;

public enum Suit {
    HEARTS("H", "♥"),
    DIAMONDS("D", "♦"),
    CLUBS("C", "♣"),
    SPADES("S", "♠");

private final String name;
private final String img;

Suit(String name,String img){
    this.name=name;
    this.img=img;
}
    public String getName(){
        return name;
    }
    public String getImage(){
        return img;
    }
@Override
public String toString(){
    return img;
}
public static Suit fromImg(String img){
    for (Suit suit : values()){
        if (suit.img.equals(img)){
            return suit;
        }
    }
    throw new IllegalArgumentException("Invalid suit symbol: " + img);
}
}

