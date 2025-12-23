#ifndef HAND_HPP
#define HAND_HPP

#include "Deck.hpp"
#include "PokerCombination.hpp"
#include <vector>

class Hand
{
private:
    vector<Card> cards;

public:
    void addCard(const Card& card);
    void clear();
    size_t size() const;
    bool isEmpty() const;

    const Card& getCard(size_t index) const;
    const vector<Card>& getAllCards() const;

    void sortByRank();
    void sortBySuit();
    void sortBestFirst();

    bool hasPair() const;
    bool isSuited() const;
    bool isConnector() const;
    bool isHighCard() const;

    bool containsRank(Rank rank) const;
    bool countRank(Rank rank) const;
    bool containsSuit(Suit suit) const;

    PokerCombination evaluateWithTable(const std::vector<Card>& tableCards) const;
};

#endif