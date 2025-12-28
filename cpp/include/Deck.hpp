#ifndef DECK_HPP
#define DECK_HPP

#include "Card.hpp"
#include <vector>

class Deck
{
private:
    std::vector<Card> cards;
    std::size_t currentIndex;

public:
    Deck();

    void shuffle();
    Card draw();
    void reset();
    std::size_t cardsRemaining() const;
    bool isEmpty() const;

    ~Deck() = default;
};

#endif