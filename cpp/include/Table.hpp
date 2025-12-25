#ifndef TABLE_HPP
#define TABLE_HPP

#include "Card.hpp"
#include <vector>

class Table
{
private:
    std::vector<Card> cards;
    int pot;
    int currentBet;

public:
    Table();

    void addCard(const Card& card);
    Card getFlopCard(int index) const;
    const std::vector<Card>& getAllFlopCards() const;
    Card getTurnCard() const;
    Card getRiverCard() const;
    const std::vector<Card>& getAllCards() const;
    void clear();
    std::size_t cardCount() const;

    void addToPot(int amount);
    int getPot() const;
    void resetPot();
    void setCurrentBet(int bet); 
    int getCurrentBet() const; 

    bool hasFlop() const;
    bool hasTurn() const;
    bool hasRiver() const;
};

#endif