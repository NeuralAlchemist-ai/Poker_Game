#ifndef CARD_HPP
#define CARD_HPP
 
#include "Rank.hpp"
#include "Suit.hpp"

class Card
{
private:
    Rank rank;
    Suit suit;

public:
    Card(Rank r, Suit s);
    
    Rank getRank() const;
    Suit getSuit() const;

    ~Card() = default;
};

#endif