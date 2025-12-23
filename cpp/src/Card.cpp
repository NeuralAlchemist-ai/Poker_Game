#include "Card.hpp"

Card::Card(Rank r, Suit s) : rank(r), suit(s) {}

Rank Card::getRank() const {
    return rank;
}

Suit Card::getSuit() const {
    return suit;
}