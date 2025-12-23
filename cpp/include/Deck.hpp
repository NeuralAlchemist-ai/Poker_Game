#ifndef DECK_HPP
#define DECK_HPP

#include "Card.hpp"
#include <vector>
using namespace std;

class Deck
{
private:
    vector<Card> cards;
    size_t currentIndex;

public:
    Deck();

    void shuffle();
    Card dealCard();
    size_t cardsRemaining();
    bool isEmpty();

    ~Deck() = default;
};

#endif