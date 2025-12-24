#ifndef DECK_HPP
#define DECK_HPP

#include "Card.hpp"
#include <vector>

class Deck
{
private:
    vector<Card> cards;
    std::size_t currentIndex;

public:
    Deck();

    void shuffle();
    Card dealCard();
    std::size_t cardsRemaining();
    bool isEmpty();

    ~Deck() = default;
};

#endif