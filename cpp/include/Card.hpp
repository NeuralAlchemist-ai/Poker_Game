#ifndef CARD_HPP
#define CARD_HPP
 
#include "Rank.hpp"
#include "Suit.hpp"
#include <string>

class Card
{
private:
    Rank rank;
    Suit suit;

public:
    Card(Rank r, Suit s);
    
    Rank getRank() const;
    Suit getSuit() const;

    bool operator<(const Card& other) const;
    bool operator>(const Card& other) const;

    static bool compareByRank(const Card& a, const Card& b);
    static bool compareBySuit(const Card& a, const Card& b);

    std::string toString() const;

    ~Card() = default;
};

#endif