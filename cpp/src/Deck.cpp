#include "Deck.hpp"
#include <algorithm>
#include <random>
using namespace std;

Deck::Deck() : currentIndex(0) {
    for (int suitVal = 0; suitVal < 4; ++suitVal) {
        for (int rankVal = 0; rankVal < 13; ++rankVal) {
            cards.push_back(Card(static_cast<Rank>(rankVal), static_cast<Suit>(suitVal)));
        }
    }
    shuffle();
}

void Deck::shuffle() {
    random_device rd;
    mt19937 gen(rd());

    std::shuffle(cards.begin(), cards.end(), gen);
}

Card Deck::dealCard() {
    if (isEmpty) throw runtime_error("Deck is empty");
    return cards[currentIndex++];
}

size_t Deck::cardsRemaining() {
    return cards.size() - currentIndex;
}

bool Deck::isEmpty() {
    return currentIndex >= cards.size();
}