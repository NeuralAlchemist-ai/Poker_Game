#include "Card.hpp"

Card::Card(Rank r, Suit s) : rank(r), suit(s) {}

Rank Card::getRank() const {
    return rank;
}

Suit Card::getSuit() const {
    return suit;
}

bool Card::operator<(const Card& other) const {
    if (rank != other.rank) {
        return static_cast<int>(rank) < static_cast<int>(other.rank);
    }

    return static_cast<int>(suit) < static_cast<int>(other.suit);
}

bool Card::operator>(const Card& other) const {
    return other < *this;
}

bool Card::compareByRank(const Card& a, const Card& b) {
    return a.rank < b.rank;
}

bool Card::compareBySuit(const Card& a, const Card& b) {
    if (a.suit != b.suit) {
        return a.suit < b.suit;
    }

    return a.rank < b.rank;
}