#ifndef TABLE_HPP
#define TABLE_HPP

#include "Card.hpp"
#include <vector>

class Table
{
private:
    std::vector<Card> cards;
    int pot;
    int sidePot;
    bool sidePotUsed;
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

    int getPot() const;
    int getSidePot() const;
    bool getSidePotUsed() const;
    int getCurrentBet() const; 
    
    void setSidePotUsed(bool used);
    void setCurrentBet(int bet); 

    void addToPot(int amount);
    void addToSidePot(int amount);
    void resetPot();
    void resetSidePot();

    bool hasFlop() const;
    bool hasTurn() const;
    bool hasRiver() const;
};

#endif