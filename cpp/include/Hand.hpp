#ifndef HAND_HPP
#define HAND_HPP

#include "Deck.hpp"
#include <vector>

class Hand
{
private:
    std::vector<Card> cards;

public:
    Hand() = default;

    void addCard(const Card& card);
    void clear();
    std::size_t size() const;
    bool isEmpty() const;

    const Card& getCard(std::size_t index) const;
    const std::vector<Card>& getAllCards() const;

    void sortByRank();
    void sortBySuit();
    void sortBestFirst();

    bool hasPocketPair() const;
    bool isSuited() const;
    bool isConnector() const;
    bool isHighCard() const;

    bool containsRank(Rank rank) const;
    int countRank(Rank rank) const;
    bool containsSuit(Suit suit) const;
    int countSuit(Suit suit) const;
    
    ~Hand() = default;
};

#endif